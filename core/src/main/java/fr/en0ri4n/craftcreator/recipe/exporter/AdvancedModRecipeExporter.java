package fr.en0ri4n.craftcreator.recipe.exporter;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;

import java.util.List;

/**
 * Exporter that can also remove existing recipes (e.g. KubeJS).
 */
public abstract class AdvancedModRecipeExporter extends ModRecipeExporter
{
    /**
     * Remove every recipe matching the filter.
     *
     * @param filter keys among "id", "type", "mod", "input", "output", e.g. {"output": "minecraft:stick"}
     */
    public abstract RecipeRequestFeedback removeRecipes(JsonObject filter);

    /**
     * @return the filters of every removal made through this exporter
     */
    public abstract List<JsonObject> getRemovals();

    /**
     * Cancel a removal made with {@link #removeRecipes(JsonObject)}.
     */
    public abstract RecipeRequestFeedback restoreRemoval(JsonObject filter);
}
