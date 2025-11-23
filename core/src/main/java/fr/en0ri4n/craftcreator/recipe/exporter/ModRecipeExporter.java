package fr.en0ri4n.craftcreator.recipe.exporter;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
public abstract class ModRecipeExporter
{
    private Path currentWorldPath;
    private boolean isLoaded = false;

    public void load(Path worldPath) {
        if(isLoaded)
            return;

        this.currentWorldPath = worldPath;
        loadInternal();
        this.isLoaded = true;
    }

    public void unload()
    {
        if(!isLoaded)
            return;
        this.currentWorldPath = null;
        this.isLoaded = false;
    }

    protected abstract void loadInternal();

    public abstract RecipeRequestFeedback addRecipe(JsonObject recipeJson);

    public abstract RecipeRequestFeedback removeAddedRecipe(Identifier id);

    public abstract List<Recipe> getRecipes();
}
