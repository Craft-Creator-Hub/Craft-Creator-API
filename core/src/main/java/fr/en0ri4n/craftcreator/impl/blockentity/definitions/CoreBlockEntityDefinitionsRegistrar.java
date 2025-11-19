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
 * <p>
 * Make sure this class is referenced during mod startup (for example from
 * CraftCreatorAPI.initialize).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreBlockEntityDefinitionsRegistrar
{
    public static void init()
    {
        // Register core block-entity behaviors first
        CoreBlockEntityBehaviorsRegistrar.init();

        // Existing crafting table recipe creator definition
        CoreBlockEntityDefinition def = CoreBlockEntityDefinition.builder(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR).inventorySize(10).addBehavior(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR).build();

        CoreBlockEntityManager.get().registerDefinition(def);

        CoreBlockEntityManager.get().lock();
    }
}