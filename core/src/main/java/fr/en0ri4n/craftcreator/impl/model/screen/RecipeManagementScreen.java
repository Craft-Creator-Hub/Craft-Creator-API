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

public class RecipeManagementScreen extends CoreScreenDefinition<RecipeManagementScreen.RecipeManagementScreenData>
{
    private CoreList<Recipe> recipeListElement;

    public RecipeManagementScreen()
    {
        super(Identifier.fromMod("recipe_management_screen"), translate("screen.recipe_management_screen.title"), new RecipeManagementScreenData(), Core2DBounds.of(0, 0, getCurrentRenderAdapter().getScreenWidth(), getCurrentRenderAdapter().getScreenHeight()));
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
        return recipes.stream().map(recipe -> new CoreList.Entry<>(recipe.getName(), recipe, recipe.getOutputs().get(0).getId(), (e) -> {})).toList();
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