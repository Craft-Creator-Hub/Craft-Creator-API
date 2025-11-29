package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityBehavior;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreator;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

/**
 * Register simple core behaviors. This class performs registration in a static way
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreBlockEntityBehaviorsRegistrar
{
    @SuppressWarnings("unchecked") // Safe because we control the behavior factory types
    public static void init()
    {
        for(RecipeCreator<?> recipeCreator : RecipeCreators.ALL_RECIPE_CREATORS)
        {
            CoreBlockEntityManager.get().registerBehavior(recipeCreator.getId(), (Supplier<BlockEntityBehavior>) recipeCreator.getBehaviorFactory());
        }
    }
}