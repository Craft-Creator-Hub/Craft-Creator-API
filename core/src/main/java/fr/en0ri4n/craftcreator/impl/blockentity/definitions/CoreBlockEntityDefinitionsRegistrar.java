package fr.en0ri4n.craftcreator.impl.blockentity.definitions;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CoreBlockEntityBehaviorsRegistrar;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreator;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Register core block-entity definitions.
 * This class executes a static registration so the definitions are available
 * when platform adapters run InitManager/BlockEntity registration.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreBlockEntityDefinitionsRegistrar
{
    public static final CoreBlockEntityDefinition CRAFTING_TABLE_RECIPE_CREATOR_DEFINITION = CoreBlockEntityDefinition.builder(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR_ID)
                                                                                                                      .inventorySize(10)
                                                                                                                      .setBehavior(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR_ID)
                                                                                                                      .build();

    public static final CoreBlockEntityDefinition FURNACE_RECIPE_CREATOR_DEFINITION = CoreBlockEntityDefinition.builder(RecipeCreators.FURNACE_RECIPE_CREATOR_ID)
                                                                                                                  .inventorySize(2)
                                                                                                                  .setBehavior(RecipeCreators.FURNACE_RECIPE_CREATOR_ID)
                                                                                                                  .build();

    public static void init()
    {
        // Register core block-entity behaviors first
        CoreBlockEntityBehaviorsRegistrar.init();

        for(RecipeCreator<?> recipeCreator : RecipeCreators.ALL_RECIPE_CREATORS)
        {
            CoreBlockEntityManager.get().registerDefinition(recipeCreator.getBlockEntityDefinition());
        }

        CoreBlockEntityManager.get().lock();
    }
}