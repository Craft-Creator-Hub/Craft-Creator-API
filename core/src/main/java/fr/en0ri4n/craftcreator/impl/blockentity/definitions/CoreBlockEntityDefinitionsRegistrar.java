package fr.en0ri4n.craftcreator.impl.blockentity.definitions;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CoreBlockEntityBehaviorsRegistrar;
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
    public static void init()
    {
        // Register core block-entity behaviors first
        CoreBlockEntityBehaviorsRegistrar.init();

        // Existing crafting table recipe creator definition
        CoreBlockEntityDefinition craftingTableRC = CoreBlockEntityDefinition.builder(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR)
                .inventorySize(10)
                .setBehavior(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR)
                .build();

        CoreBlockEntityDefinition furnaceRC = CoreBlockEntityDefinition.builder(RecipeCreators.FURNACE_RECIPE_CREATOR)
                .inventorySize(2)
                .setBehavior(RecipeCreators.FURNACE_RECIPE_CREATOR)
                .build();

        CoreBlockEntityManager.get().registerDefinition(
                craftingTableRC,
                furnaceRC
        );

        CoreBlockEntityManager.get().lock();
    }
}