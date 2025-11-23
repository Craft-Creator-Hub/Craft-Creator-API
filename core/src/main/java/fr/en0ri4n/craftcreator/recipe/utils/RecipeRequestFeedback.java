package fr.en0ri4n.craftcreator.recipe.utils;

import fr.en0ri4n.craftcreator.utils.Feedback;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeRequestFeedback
{
    private final Feedback feedback;
    private final boolean success;

    public static RecipeRequestFeedback of(Feedback feedback, boolean success)
    {
        return new RecipeRequestFeedback(feedback, success);
    }
}
