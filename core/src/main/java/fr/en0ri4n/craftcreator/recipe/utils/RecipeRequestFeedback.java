package fr.en0ri4n.craftcreator.recipe.utils;

import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.utils.Feedback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class RecipeRequestFeedback
{
    private final Feedback feedback;
    private final boolean success;

    private SupportedRecipeExporter recipeType;
    private String recipeName;
    private String recipeJson;
    private String recipePath;

    public static RecipeRequestFeedback of(Feedback feedback, boolean success)
    {
        return of(feedback, success, null, null, null, null);
    }

    public static RecipeRequestFeedback of(Feedback feedback, boolean success, SupportedRecipeExporter recipeType, String id, String recipeJson, String recipePath)
    {
        return new RecipeRequestFeedback(feedback, success, recipeType, id, recipeJson, recipePath);
    }
}
