package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.recipe.exporter.ModRecipeExporter;
import fr.en0ri4n.craftcreator.recipe.exporter.RecipeExporterRegistry;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RecipeManagementScreen extends CoreScreenDefinition<RecipeManagementScreen.RecipeManagementScreenData>
{
    private CoreList<Recipe> recipeListElement;

    public RecipeManagementScreen()
    {
        super(Identifier.fromMod("recipe_management_screen"), "screen.recipe_management_screen.title", new RecipeManagementScreenData(), Core2DBounds.of(0, 0, getCurrentRenderAdapter().getScreenWidth(), getCurrentRenderAdapter().getScreenHeight()));
    }

    @Override
    protected void initElements(int screenWidth, int screenHeight)
    {
        addElement(recipeListElement = new CoreList<>(10, 10, 180, screenHeight - 20, 20, transformRecipesToEntries(RecipeManagementScreenData.loadedRecipes)));
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        this.recipeListElement.renderForeground(ctx, mouseX, mouseY);
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
            int relativeX = mouseX + 12;
            int relativeY = mouseY + 12;
            getRenderAdapter().drawRect(ctx, relativeX, relativeY, 100, 40, 0xCC004B96);
            getRenderAdapter().drawText(ctx, "Type: " + getValue().getType(), relativeX += 3, relativeY += 3, 0xFFFFFFFF);
            getRenderAdapter().drawText(ctx, "Input(s):", relativeX, relativeY += 16, 0xFFFFFFFF);
            for(int i = 0; i < getValue().getInputs().size(); i++)
                getRenderAdapter().drawText(ctx, "- " + getValue().getInputs().get(i).getId(), relativeX + 12, relativeY += 12, 0xFFFFFFFF);
            getRenderAdapter().drawText(ctx, "Output: " + getValue().getOutputs().get(0).getId(), relativeX, relativeY += 16, 0xFFFFFFFF);
            getRenderAdapter().drawText(ctx, "Recipe Parameters:" + (getValue().getInfos().getParameters().isEmpty() ? " None" : ""), relativeX, relativeY += 16, 0xFFFFFFFF);
            for(int i = 0; i < getValue().getInfos().getParameters().size(); i++)
                getRenderAdapter().drawText(ctx, "- " + getValue().getInfos().getParameters().get(i).getName() + ": " + getValue().getInfos().getParameters().get(i).getRawValue(), relativeX + 12, relativeY += 12, 0xFFFFFFFF);
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
}