package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTextInput;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.recipe.exporter.ModRecipeExporter;
import fr.en0ri4n.craftcreator.recipe.exporter.RecipeExporterRegistry;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class RecipeManagementScreen extends CoreScreenDefinition<RecipeManagementScreen.RecipeManagementScreenData>
{
    private final Map<RecipeListType, CoreButton> recipeListTypeButtons = new HashMap<>();
    private CoreList<Recipe> recipeListElement;

    public RecipeManagementScreen()
    {
        super(Identifier.fromMod("recipe_management_screen"), "screen.recipe_management_screen.title", new RecipeManagementScreenData(), Core2DBounds.of(0, 0, getCurrentRenderAdapter().getScreenWidth(), getCurrentRenderAdapter().getScreenHeight()));
    }

    @Override
    protected void initElements(int screenWidth, int screenHeight)
    {
        addElement(recipeListElement = new CoreList<>(10, 75, 180, screenHeight - 120, 20, transformRecipesToEntries(RecipeManagementScreenData.loadedRecipes)));
        
        recipeListTypeButtons.clear();

        addElement(new CoreDropdown<>("recipe_exporter_dropdown", getGuiSize().getRight(-130), getGuiSize().getBottom(-30), 120, 18, true, List.of(SupportedRecipeExporter.values()), 0, "Exporter", (selected) -> {
            if(selected != null)
                RecipeManagementScreenData.loadRecipes(selected);
        }));

        int relativeY = 30;
        int relativeX = 10;
        int width = 85;
        int height = 18;
        addElement(addRecipeListTypeButton(RecipeListType.RECIPES, new CoreButton("Recipes", relativeX, relativeY, width, height, "All",
                () -> setCurrentRecipeListType(RecipeListType.RECIPES), "Export all loaded recipes to a Minecraft datapack in the exports folder.")));
        addElement(addRecipeListTypeButton(RecipeListType.ADDED_RECIPES, new CoreButton("Added", relativeX + width + 10, relativeY, width, height, "Added",
                () -> setCurrentRecipeListType(RecipeListType.ADDED_RECIPES), "View the list of recipes you've added during this session.")));
        addElement(addRecipeListTypeButton(RecipeListType.MODIFIED_RECIPES, new CoreButton("Modified", relativeX, relativeY + 20, width, height, "Modified",
                () -> setCurrentRecipeListType(RecipeListType.MODIFIED_RECIPES), "View the list of recipes you've modified during this session.")));
        addElement(addRecipeListTypeButton(RecipeListType.DELETED_RECIPES, new CoreButton("Deleted", relativeX + width + 10, relativeY + 20, width, height, "Deleted",
                () -> setCurrentRecipeListType(RecipeListType.DELETED_RECIPES), "View the list of recipes you've deleted during this session.")));
        
        setCurrentRecipeListType(RecipeListType.RECIPES);
        
        addElement(new CoreTextInput("search", CoreTextInput.TextInputType.STRING, 10, screenHeight - 30, 180, height, "Search for recipes", "", "Recipe...", 64, "Type to search"));

        addElement(new CoreButton("close", getGuiSize().getHorizontalCenter(80), screenHeight - 30, 80, height, "Close", this::closeScreen, null));
    }

    private void closeScreen()
    {
        getCurrentUiAdapter().closeScreen();
    }

    private Recipe getCurrentSelectedRecipe()
    {
        Optional<CoreList.Entry<Recipe>> selectedEntry = recipeListElement.getSelected();
        return selectedEntry.map(CoreList.Entry::getValue).orElse(null);
    }
    
    private CoreButton addRecipeListTypeButton(RecipeListType type, CoreButton button)
    {
        recipeListTypeButtons.put(type, button);
        return button;
    }
    
    private void setCurrentRecipeListType(RecipeListType type)
    {        
        recipeListTypeButtons.forEach((t, b) -> b.setEnabled(t != type));
        
        switch (type)
        {
            case RECIPES -> this.recipeListElement.setEntries(transformRecipesToEntries(RecipeManagementScreenData.loadedRecipes));
            case ADDED_RECIPES, MODIFIED_RECIPES, DELETED_RECIPES -> this.recipeListElement.setEntries(new ArrayList<>());
        }
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        getCurrentRenderAdapter().drawRect(ctx, getGuiSize().getX(200), getGuiSize().getY(30), getGuiSize().getX(getGuiSize().getWidth(-210)), getGuiSize().getX(getGuiSize().getHeight(-75)), 0x80000000);

        int relativeX = recipeListElement.getBounds().getRight() + 10;

        if(getCurrentSelectedRecipe() != null)
        {
            getCurrentRenderAdapter().drawText(ctx, "ID: " + getCurrentSelectedRecipe().getName(), relativeX, getGuiSize().getY(55), 0xFFFFFF);
            getCurrentRenderAdapter().drawText(ctx, "Type: " + getCurrentSelectedRecipe().getType(), relativeX, getGuiSize().getY(75), 0xFFFFFF);
            getCurrentRenderAdapter().drawText(ctx, "Inputs:", relativeX, getGuiSize().getY(95), 0xFFFFFF);
            for(int i = 0; i < getCurrentSelectedRecipe().getInputs().size(); i++)
                getCurrentRenderAdapter().drawText(ctx, "- " + getCurrentSelectedRecipe().getInputs().get(i).getId(), relativeX + 10, getGuiSize().getY(115 + i * 20), 0xFFFFFF);
            getCurrentRenderAdapter().drawText(ctx, "Output: " + getCurrentSelectedRecipe().getOutputs().get(0).getId(), relativeX, getGuiSize().getY(135 + getCurrentSelectedRecipe().getInputs().size() * 20), 0xFFFFFF);
        }
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        this.recipeListElement.renderForeground(ctx, mouseX, mouseY);

        String title = translate(getTitle());
        int titleWidth = getCurrentRenderAdapter().getTextWidth(title);
        getCurrentRenderAdapter().drawText(ctx, title, getGuiSize().getHorizontalCenter(titleWidth), getGuiSize().getY(10), 0xFFFFFF);

        super.renderForeground(ctx, mouseX, mouseY);
    }

    @Override
    public void fetchData()
    {
        RecipeManagementScreenData.loadRecipes(SupportedRecipeExporter.MINECRAFT_DATAPACK);
    }

    protected List<CoreList.Entry<Recipe>> transformRecipesToEntries(List<Recipe> recipes)
    {
        return recipes.stream().<CoreList.Entry<Recipe>>map(recipe -> new RecipeEntry(recipe.getName(), recipe, recipe.getOutputs().get(0).getId(), (e) -> {})).toList();
    }

    public static class RecipeEntry extends CoreList.Entry<Recipe>
    {
        public RecipeEntry(String displayName, Recipe data, Identifier id, Consumer<CoreList.Entry<Recipe>> onClick)
        {
            super(displayName, data, id, onClick);
        }

        @Override
        public void renderForeground(RenderContext ctx, int index, int x, int y, int width, int itemHeight, int mouseX, int mouseY, boolean selected, boolean hovered)
        {
            List<String> tooltips = new ArrayList<>();
            tooltips.add("§aType: " + getValue().getType());
            tooltips.add("§6Input(s):");
            for(int i = 0; i < getValue().getInputs().size(); i++)
                tooltips.add("  - " + getValue().getInputs().get(i).getId());
            tooltips.add("§bOutput: " + getValue().getOutputs().get(0).getId());
            tooltips.add("§7Recipe Parameters:§r" + (getValue().getInfos().getParameters().isEmpty() ? " None" : ""));
            for(int i = 0; i < getValue().getInfos().getParameters().size(); i++)
                tooltips.add("  - " + getValue().getInfos().getParameters().get(i).getName() + ": " + getValue().getInfos().getParameters().get(i).getRawValue());
            getRenderAdapter().drawTooltips(ctx, tooltips, mouseX, mouseY);
        }
    }

    public static class RecipeManagementScreenData implements ScreenData
    {
        protected static List<Recipe> loadedRecipes = new ArrayList<>();

        protected static void loadRecipes(SupportedRecipeExporter exporter)
        {
            loadedRecipes.clear();
            ModRecipeExporter modExporter = RecipeExporterRegistry.get().getExporter(exporter);
            loadedRecipes.addAll(modExporter.getRecipes());
        }

        @Override
        public void load(JsonObject jsonObject) {}

        @Override
        public JsonObject save() { return new JsonObject(); }
    }
    
    public enum RecipeListType
    {
        RECIPES,
        ADDED_RECIPES,
        MODIFIED_RECIPES,
        DELETED_RECIPES,
    }
}