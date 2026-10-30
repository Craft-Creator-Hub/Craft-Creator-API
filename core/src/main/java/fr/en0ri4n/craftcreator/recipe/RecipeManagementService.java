package fr.en0ri4n.craftcreator.recipe;

import fr.en0ri4n.craftcreator.api.net.RecipeManagementRequest;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementState;
import fr.en0ri4n.craftcreator.recipe.exporter.AdvancedModRecipeExporter;
import fr.en0ri4n.craftcreator.recipe.exporter.ModRecipeExporter;
import fr.en0ri4n.craftcreator.recipe.exporter.RecipeExporterRegistry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.utils.Feedback;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

/**
 * Server side of the recipe management screens: runs the requested action on the exporter and returns its new state.
 */
public class RecipeManagementService
{
    private static final RecipeManagementService INSTANCE = new RecipeManagementService();
    public static RecipeManagementService get() { return INSTANCE; }

    public RecipeManagementState handle(RecipeManagementRequest request)
    {
        ModRecipeExporter exporter = RecipeExporterRegistry.get().getExporter(request.getExporter());
        AdvancedModRecipeExporter advancedExporter = exporter instanceof AdvancedModRecipeExporter advanced ? advanced : null;

        RecipeRequestFeedback result = switch(request.getAction())
        {
            case LIST -> null;
            case DELETE_ADDED -> request.getPayload().has("id")
                    ? exporter.removeAddedRecipe(Identifier.fromMod(request.getPayload().get("id").getAsString()))
                    : RecipeRequestFeedback.of(Feedback.INVALID_JSON_RECIPE, false);
            case REMOVE -> advancedExporter != null ? advancedExporter.removeRecipes(request.getPayload()) : RecipeRequestFeedback.of(Feedback.NOT_SUPPORTED, false);
            case RESTORE -> advancedExporter != null ? advancedExporter.restoreRemoval(request.getPayload()) : RecipeRequestFeedback.of(Feedback.NOT_SUPPORTED, false);
            case REPLACE -> advancedExporter != null ? advancedExporter.addReplacement(request.getPayload()) : RecipeRequestFeedback.of(Feedback.NOT_SUPPORTED, false);
            case CANCEL_REPLACEMENT -> advancedExporter != null ? advancedExporter.cancelReplacement(request.getPayload()) : RecipeRequestFeedback.of(Feedback.NOT_SUPPORTED, false);
        };

        return new RecipeManagementState(request.getExporter(),
                                         exporter.getAddedRecipesJson(),
                                         advancedExporter != null ? advancedExporter.getRemovals() : List.of(),
                                         advancedExporter != null ? advancedExporter.getReplacements() : List.of(),
                                         advancedExporter != null,
                                         result == null ? null : result.getFeedback(),
                                         result == null || result.isSuccess());
    }
}
