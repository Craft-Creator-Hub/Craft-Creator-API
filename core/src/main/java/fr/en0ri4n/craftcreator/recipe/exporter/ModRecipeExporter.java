package fr.en0ri4n.craftcreator.recipe.exporter;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializer;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializerRegistry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

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

    public boolean doesRecipeExist(String recipeId)
    {
        return getAddedRecipesJson().stream().anyMatch(json -> json.has("id") && json.get("id").getAsString().equals(recipeId));
    }

    protected abstract void loadInternal();

    public abstract RecipeRequestFeedback addRecipe(JsonObject recipeJson);

    public abstract RecipeRequestFeedback removeAddedRecipe(Identifier id);

    /**
     * @return the json of every recipe added through this exporter
     */
    public abstract List<JsonObject> getAddedRecipesJson();

    /**
     * @return the recipes added through this exporter, deserialized for display
     */
    public List<Recipe> getRecipes()
    {
        return getAddedRecipesJson().stream().map(ModRecipeExporter::toRecipe).filter(Objects::nonNull).toList();
    }

    /**
     * Deserialize a recipe json with the serializer of its type.
     *
     * @return the recipe, or null if its type isn't supported
     */
    public static Recipe toRecipe(JsonObject recipeJson)
    {
        if(!recipeJson.has("type")) return null;

        RecipeSerializer serializer = RecipeSerializerRegistry.get().getByRecipeTypeId(Identifier.from(recipeJson.get("type").getAsString()));
        if(serializer == null) return null;

        Recipe recipe = serializer.deserializeToRecipe(recipeJson);
        return recipe == Recipe.EMPTY ? null : recipe;
    }
}
