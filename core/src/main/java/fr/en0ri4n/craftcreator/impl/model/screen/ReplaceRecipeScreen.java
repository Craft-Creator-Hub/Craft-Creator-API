package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedMod;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementRequest;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementState;
import fr.en0ri4n.craftcreator.api.platform.GameDataProvider;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreComboBox;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTheme;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeManagementListener;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.recipe.RecipeFilters;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Replace an item in the inputs or outputs of recipes with KubeJS (event.replaceInput / event.replaceOutput).
 * Every field can be typed in or picked from its dropdown. The filter fields are optional: empty, every recipe using
 * the replaced item is changed. The number of affected recipes is computed from the client's recipes while typing.
 */
public class ReplaceRecipeScreen extends CoreScreenDefinition<ReplaceRecipeScreen.ReplaceRecipeScreenData> implements RecipeManagementListener
{
    private static final int FIELD_WIDTH = 220;
    private static final int ROW = 24;
    private static final int MESSAGE_DURATION_MS = 5000;

    private final CoreScreenDefinition<?> parent;
    /** Recipe the screen was opened for, its items are suggested first. Null when opened from the toolbar. */
    private final Recipe recipe;
    private final List<Recipe> gameRecipes;

    private final Map<Field, CoreComboBox> fields = new EnumMap<>(Field.class);
    private Kind kind = Kind.INPUT;
    private int affected;

    private String message;
    private int messageColor;
    private long messageTime;

    public ReplaceRecipeScreen(CoreScreenDefinition<?> parent, Recipe recipe)
    {
        super(Identifier.fromMod("replace_recipe_screen"), "screen.replace_manager.title", new ReplaceRecipeScreenData(), Core2DBounds.ofSize(FIELD_WIDTH + 20, 212));
        this.parent = parent;
        this.recipe = recipe;
        this.gameRecipes = provider().getClientRecipes();
    }

    @Override
    protected void initElements(int screenWidth, int screenHeight)
    {
        Map<Field, String> previousValues = new EnumMap<>(Field.class);
        fields.forEach((field, combo) -> previousValues.put(field, combo.getValue()));
        fields.clear();

        Core2DBounds gui = getGuiSize();
        int x = gui.getX(10);

        addElement(new CoreDropdown<>("kind", x, gui.getY(10), FIELD_WIDTH, 16, true, List.of(Kind.values()), kind.ordinal(), "Part of the recipes to change", this::setKind)
        {
            @Override
            public String getSelectedValueAsString()
            {
                return getSelectedValue() == Kind.INPUT ? "Replace in inputs" : "Replace in outputs";
            }
        });

        for(Field field : Field.values())
        {
            int y = gui.getY(40 + field.ordinal() * ROW + (field.isFilter ? 14 : 0));
            String initial = previousValues.getOrDefault(field, field == Field.ID && recipe != null ? recipe.getId().toString() : "");
            CoreComboBox combo = new CoreComboBox(field.key, x, y, FIELD_WIDTH, 14, field.label, initial, field.placeholder, field.tooltip, () -> getValues(field));
            combo.setOnChange(value -> updateAffected());
            fields.put(field, combo);
            addElement(combo);
        }

        CoreButton replaceButton = new CoreButton("replace", x, gui.getBottom(-28), 90, 18, "Replace", this::replace, "Write the replacement in the KubeJS script");
        replaceButton.setEnabled(SupportedMod.isKubeJSLoaded());
        addElement(replaceButton);
        addElement(new CoreButton("back", x + 94, gui.getBottom(-28), 50, 18, translateToString("screen.button.back"), () -> getCurrentUiAdapter().openScreen(parent), null));

        updateAffected();
    }

    /* ---------------- fields ---------------- */

    private void setKind(Kind newKind)
    {
        if(newKind == null || newKind == kind) return;
        kind = newKind;
        // What can be replaced depends on the kind
        fields.get(Field.FROM).setValues(() -> getValues(Field.FROM));
        fields.get(Field.TO).setValues(() -> getValues(Field.TO));
        updateAffected();
    }

    /**
     * Items of the recipe the screen was opened for come first, then everything else that fits the field.
     */
    private List<String> getValues(Field field)
    {
        Set<String> values = new LinkedHashSet<>();
        switch(field)
        {
            case FROM ->
            {
                if(recipe != null)
                {
                    List<RecipeEntry> entries = kind == Kind.INPUT ? recipe.getInputs() : recipe.getOutputs();
                    entries.forEach(entry -> values.add((entry.isTag() ? "#" : "") + entry.getId()));
                }
                values.addAll(RemoveRecipeScreen.itemsAndTags(kind == Kind.INPUT));
            }
            case TO -> values.addAll(RemoveRecipeScreen.itemsAndTags(kind == Kind.INPUT));
            case ID -> gameRecipes.forEach(r -> values.add(r.getId().toString()));
            case TYPE -> provider().getRecipeTypeIds().forEach(id -> values.add(id.toString()));
            case MOD -> values.addAll(provider().getModIds());
        }
        return new ArrayList<>(values);
    }

    private String value(Field field)
    {
        CoreComboBox combo = fields.get(field);
        return combo == null ? "" : combo.getValue().trim();
    }

    /* ---------------- replacement ---------------- */

    /**
     * @return the replacement, or null if what and with are missing
     */
    private JsonObject buildReplacement()
    {
        String from = value(Field.FROM);
        String to = value(Field.TO);
        if(from.isEmpty() || to.isEmpty()) return null;

        JsonObject filter = new JsonObject();
        for(Field field : Field.values())
            if(field.isFilter && !value(field).isEmpty()) filter.addProperty(field.key, value(field));
        // Without filter, target the recipes using the replaced item (as in the KubeJS examples)
        if(filter.size() == 0) filter.addProperty(kind == Kind.INPUT ? "input" : "output", from);

        JsonObject replacement = new JsonObject();
        replacement.addProperty("kind", kind == Kind.INPUT ? "input" : "output");
        replacement.add("filter", filter);
        replacement.addProperty("from", from);
        replacement.addProperty("to", to);
        return replacement;
    }

    private void updateAffected()
    {
        JsonObject replacement = buildReplacement();
        affected = replacement == null ? 0 : (int) gameRecipes.stream().filter(r -> RecipeFilters.affects(replacement, r)).count();
    }

    private void replace()
    {
        JsonObject replacement = buildReplacement();
        if(replacement == null)
        {
            showMessage("Fill what to replace and the replacement", false);
            return;
        }
        if(replacement.get("from").getAsString().equals(replacement.get("to").getAsString()))
        {
            showMessage("The replacement is the same item", false);
            return;
        }
        if(kind == Kind.OUTPUT && replacement.get("to").getAsString().startsWith("#"))
        {
            showMessage("An output must be an item, not a tag", false);
            return;
        }

        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter()
                .sendManagementRequestToServer(new RecipeManagementRequest(RecipeManagementRequest.Action.REPLACE, SupportedRecipeExporter.KUBE_JS, replacement));
    }

    @Override
    public void onManagementState(RecipeManagementState state)
    {
        if(state.getFeedback() != null)
            showMessage(translateToString(state.getFeedback().getMessageKey(), "") + (state.isSuccess() ? " - /reload to apply" : ""), state.isSuccess());
    }

    private void showMessage(String text, boolean success)
    {
        message = text;
        messageColor = success ? CoreTheme.TEXT_SUCCESS : CoreTheme.TEXT_ERROR;
        messageTime = System.currentTimeMillis();
    }

    /* ---------------- rendering ---------------- */

    @Override
    public void renderBackground(RenderContext ctx)
    {
        super.renderBackground(ctx);
        renderTextureWithBounds(ctx, GUI_TEXTURE, getGuiSize(), false, false);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        Core2DBounds gui = getGuiSize();
        int x = gui.getX(10);

        CoreComboBox firstFilter = fields.get(Field.ID);
        if(firstFilter != null)
            getRenderAdapter().drawText(ctx, "Only in recipes matching (optional)", x, firstFilter.getBounds().getY() - 20, CoreTheme.TEXT_TITLE);

        int infoY = gui.getBottom(-42);
        if(!SupportedMod.isKubeJSLoaded())
            getRenderAdapter().drawText(ctx, "KubeJS is needed to replace items", x, infoY, CoreTheme.TEXT_ERROR);
        else if(message != null && System.currentTimeMillis() - messageTime < MESSAGE_DURATION_MS)
            getRenderAdapter().drawText(ctx, message, x, infoY, messageColor);
        else if(buildReplacement() != null)
            getRenderAdapter().drawText(ctx, affected + " recipe(s) affected", x, infoY, affected > 0 ? CoreTheme.TEXT : CoreTheme.TEXT_ERROR);
    }

    /* ---------------- types ---------------- */

    private enum Kind
    {
        INPUT,
        OUTPUT
    }

    private enum Field
    {
        FROM("from", "Replace", "minecraft:stick", "Item or #tag to replace", false),
        TO("to", "With", "#minecraft:saplings", "New item (or #tag for inputs)", false),
        ID("id", "Recipe id", "minecraft:torch", "Only this recipe", true),
        TYPE("type", "Recipe type", "minecraft:crafting_shaped", "Only recipes of this type", true),
        MOD("mod", "Mod", "minecraft", "Only recipes of this mod", true);

        private final String key;
        private final String label;
        private final String placeholder;
        private final String tooltip;
        private final boolean isFilter;

        Field(String key, String label, String placeholder, String tooltip, boolean isFilter)
        {
            this.key = key;
            this.label = label;
            this.placeholder = placeholder;
            this.tooltip = tooltip;
            this.isFilter = isFilter;
        }
    }

    private static GameDataProvider provider()
    {
        return CraftCreatorAPI.get().getPlatform().getGameDataProvider();
    }

    public static class ReplaceRecipeScreenData implements ScreenData
    {
        @Override
        public void load(JsonObject jsonObject) {}

        @Override
        public JsonObject save() { return new JsonObject(); }
    }
}
