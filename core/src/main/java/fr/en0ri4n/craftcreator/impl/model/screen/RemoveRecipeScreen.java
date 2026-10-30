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
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTextInput;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTheme;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeManagementListener;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Remove every recipe matching a filter with KubeJS. Empty fields are ignored; the list on the right suggests values
 * for the field being typed in.
 */
public class RemoveRecipeScreen extends CoreScreenDefinition<RemoveRecipeScreen.RemoveRecipeScreenData> implements RecipeManagementListener
{
    private static final int FIELD_WIDTH = 170;
    private static final int MESSAGE_DURATION_MS = 5000;

    private final CoreScreenDefinition<?> parent;
    private final Map<Field, CoreTextInput> inputs = new LinkedHashMap<>();
    private CoreList<String> suggestions;
    private Field activeField;

    private String message;
    private int messageColor;
    private long messageTime;

    public RemoveRecipeScreen(CoreScreenDefinition<?> parent)
    {
        super(Identifier.fromMod("remove_recipe_screen"), "screen.remove_manager.title", new RemoveRecipeScreenData(), Core2DBounds.ofSize(370, 210));
        this.parent = parent;
    }

    @Override
    protected void initElements(int screenWidth, int screenHeight)
    {
        inputs.clear();
        Core2DBounds gui = getGuiSize();

        for(Field field : Field.values())
        {
            CoreTextInput input = new CoreTextInput(field.key, CoreTextInput.TextInputType.STRING, gui.getX(10), gui.getY(24 + field.ordinal() * 30), FIELD_WIDTH, 14,
                                                    translateToString(field.labelKey), "", field.placeholder, 256, field.tooltip);
            input.setOnChange(value -> onFieldChanged(field, value));
            inputs.put(field, input);
            addElement(input);
        }

        addElement(suggestions = new CoreList<>(gui.getX(FIELD_WIDTH + 20), gui.getY(10), gui.getWidth() - FIELD_WIDTH - 30, gui.getHeight() - 46, 12, List.of()));
        suggestions.setEmptyText("Type in a field");
        activeField = null;

        CoreButton removeButton = new CoreButton("remove", gui.getX(10), gui.getBottom(-28), 120, 18, translateToString("screen.remove_manager.button.remove"), this::remove,
                                                 "Remove every recipe matching all the filled fields");
        removeButton.setEnabled(SupportedMod.isKubeJSLoaded());
        addElement(removeButton);
        addElement(new CoreButton("back", gui.getX(140), gui.getBottom(-28), 50, 18, translateToString("screen.button.back"), () -> getCurrentUiAdapter().openScreen(parent), null));
    }

    private void onFieldChanged(Field field, String value)
    {
        if(field != activeField)
        {
            activeField = field;
            suggestions.setEntries(field.values.get().stream().map(s -> new CoreList.Entry<>(s, s, this::applySuggestion)).toList());
        }
        suggestions.filterByLabel(value);
    }

    private void applySuggestion(CoreList.Entry<String> entry)
    {
        if(activeField != null) inputs.get(activeField).setValue(entry.getValue());
    }

    private void remove()
    {
        JsonObject filter = new JsonObject();
        inputs.forEach((field, input) ->
        {
            String value = input.getValue().trim();
            if(!value.isEmpty()) filter.addProperty(field.key, value);
        });

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
            showMessage(translateToString(state.getFeedback().getMessageKey(), "") + (state.isSuccess() ? " (/reload to apply)" : ""), state.isSuccess());
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
        if(!SupportedMod.isKubeJSLoaded())
            getRenderAdapter().drawText(ctx, "KubeJS is needed to remove recipes", gui.getX(10), gui.getBottom(-40), CoreTheme.TEXT_ERROR);
        else if(message != null && System.currentTimeMillis() - messageTime < MESSAGE_DURATION_MS)
            getRenderAdapter().drawText(ctx, message, gui.getX(10), gui.getBottom(-40), messageColor);
    }

    private enum Field
    {
        ID("id", "screen.remove_manager.button.has_id", "minecraft:stick", "Id of the recipe", () -> provider().getClientRecipes().stream().map(Recipe::getId).map(Identifier::toString).toList()),
        TYPE("type", "screen.remove_manager.button.has_type", "minecraft:smelting", "Type of the recipes", () -> provider().getRecipeTypeIds().stream().map(Identifier::toString).toList()),
        MOD("mod", "screen.remove_manager.button.has_mod", "minecraft", "Mod adding the recipes", () -> provider().getModIds()),
        INPUT("input", "screen.remove_manager.button.has_input_item", "minecraft:oak_planks", "Item or #tag used by the recipes", RemoveRecipeScreen::itemIds),
        OUTPUT("output", "screen.remove_manager.button.has_output_item", "minecraft:stick", "Item made by the recipes", RemoveRecipeScreen::itemIds);

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

    private static List<String> itemIds()
    {
        return provider().getItemIds().stream().map(Identifier::toString).toList();
    }

    public static class RemoveRecipeScreenData implements ScreenData
    {
        @Override
        public void load(JsonObject jsonObject) {}

        @Override
        public JsonObject save() { return new JsonObject(); }
    }
}
