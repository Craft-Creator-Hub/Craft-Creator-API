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
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTheme;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeManagementListener;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.recipe.RecipeFilters;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Remove every recipe matching a filter with KubeJS. Empty fields are ignored; each field can be typed in or picked
 * from its dropdown.
 */
public class RemoveRecipeScreen extends CoreScreenDefinition<RemoveRecipeScreen.RemoveRecipeScreenData> implements RecipeManagementListener
{
    private static final int FIELD_WIDTH = 220;
    private static final int ROW = 26;
    private static final int MESSAGE_DURATION_MS = 5000;

    private final CoreScreenDefinition<?> parent;
    private final List<Recipe> gameRecipes;
    private final Map<Field, CoreComboBox> fields = new EnumMap<>(Field.class);

    private String message;
    private int messageColor;
    private long messageTime;

    public RemoveRecipeScreen(CoreScreenDefinition<?> parent)
    {
        super(Identifier.fromMod("remove_recipe_screen"), "screen.remove_manager.title", new RemoveRecipeScreenData(), Core2DBounds.ofSize(FIELD_WIDTH + 20, 200));
        this.parent = parent;
        this.gameRecipes = provider().getClientRecipes();
    }

    @Override
    protected void initElements(int screenWidth, int screenHeight)
    {
        Map<Field, String> previousValues = new EnumMap<>(Field.class);
        fields.forEach((field, combo) -> previousValues.put(field, combo.getValue()));
        fields.clear();

        Core2DBounds gui = getGuiSize();
        for(Field field : Field.values())
        {
            CoreComboBox combo = new CoreComboBox(field.key, gui.getX(10), gui.getY(20 + field.ordinal() * ROW), FIELD_WIDTH, 14,
                                                  translateToString(field.labelKey), previousValues.getOrDefault(field, ""), field.placeholder, field.tooltip, field.values);
            fields.put(field, combo);
            addElement(combo);
        }

        CoreButton removeButton = new CoreButton("remove", gui.getX(10), gui.getBottom(-28), 120, 18, translateToString("screen.remove_manager.button.remove"), this::remove,
                                                 "Remove every recipe matching all the filled fields");
        removeButton.setEnabled(SupportedMod.isKubeJSLoaded());
        addElement(removeButton);
        addElement(new CoreButton("back", gui.getX(134), gui.getBottom(-28), 50, 18, translateToString("screen.button.back"), () -> getCurrentUiAdapter().openScreen(parent), null));
    }

    /**
     * @return the filter made of the filled fields
     */
    private JsonObject buildFilter()
    {
        JsonObject filter = new JsonObject();
        fields.forEach((field, combo) ->
        {
            String value = combo.getValue().trim();
            if(!value.isEmpty()) filter.addProperty(field.key, value);
        });
        return filter;
    }

    private void remove()
    {
        JsonObject filter = buildFilter();
        if(filter.size() == 0)
        {
            showMessage("Fill at least one field", false);
            return;
        }

        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter()
                .sendManagementRequestToServer(new RecipeManagementRequest(RecipeManagementRequest.Action.REMOVE, SupportedRecipeExporter.KUBE_JS, filter));
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
        int y = gui.getBottom(-42);

        if(!SupportedMod.isKubeJSLoaded())
            getRenderAdapter().drawText(ctx, "KubeJS is needed to remove recipes", gui.getX(10), y, CoreTheme.TEXT_ERROR);
        else if(message != null && System.currentTimeMillis() - messageTime < MESSAGE_DURATION_MS)
            getRenderAdapter().drawText(ctx, message, gui.getX(10), y, messageColor);
        else
        {
            JsonObject filter = buildFilter();
            if(filter.size() > 0)
            {
                long count = gameRecipes.stream().filter(recipe -> RecipeFilters.matches(filter, recipe)).count();
                getRenderAdapter().drawText(ctx, count + " recipe(s) will be removed", gui.getX(10), y, count > 0 ? CoreTheme.TEXT : CoreTheme.TEXT_ERROR);
            }
        }
    }

    private enum Field
    {
        ID("id", "screen.remove_manager.button.has_id", "minecraft:stick", "Id of the recipe", () -> provider().getClientRecipes().stream().map(Recipe::getId).map(Identifier::toString).toList()),
        TYPE("type", "screen.remove_manager.button.has_type", "minecraft:smelting", "Type of the recipes", () -> provider().getRecipeTypeIds().stream().map(Identifier::toString).toList()),
        MOD("mod", "screen.remove_manager.button.has_mod", "minecraft", "Mod adding the recipes", () -> provider().getModIds()),
        INPUT("input", "screen.remove_manager.button.has_input_item", "minecraft:oak_planks", "Item or #tag used by the recipes", () -> itemsAndTags(true)),
        OUTPUT("output", "screen.remove_manager.button.has_output_item", "minecraft:stick", "Item made by the recipes", () -> itemsAndTags(false));

        private final String key;
        private final String labelKey;
        private final String placeholder;
        private final String tooltip;
        private final Supplier<List<String>> values;

        Field(String key, String labelKey, String placeholder, String tooltip, Supplier<List<String>> values)
        {
            this.key = key;
            this.labelKey = labelKey;
            this.placeholder = placeholder;
            this.tooltip = tooltip;
            this.values = values;
        }
    }

    private static GameDataProvider provider()
    {
        return CraftCreatorAPI.get().getPlatform().getGameDataProvider();
    }

    static List<String> itemsAndTags(boolean withTags)
    {
        List<String> values = new ArrayList<>(provider().getItemIds().stream().map(Identifier::toString).toList());
        if(withTags) provider().getItemTagIds().forEach(tag -> values.add("#" + tag));
        return values;
    }

    public static class RemoveRecipeScreenData implements ScreenData
    {
        @Override
        public void load(JsonObject jsonObject) {}

        @Override
        public JsonObject save() { return new JsonObject(); }
    }
}
