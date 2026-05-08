package fr.en0ri4n.craftcreator.api.translations;

import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;

public interface TextComponentProvider<C>
{
    String translateToString(String key, Object... args);

    C translate(String key, Object... args);

    C literal(String text);

    C createFeedbackComponent(RecipeRequestFeedback feedback);
}

