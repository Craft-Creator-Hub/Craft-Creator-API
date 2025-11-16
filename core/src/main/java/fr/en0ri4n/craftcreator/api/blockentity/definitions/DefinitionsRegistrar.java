package fr.en0ri4n.craftcreator.api.blockentity.definitions;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import fr.en0ri4n.craftcreator.api.blockentity.behaviors.BehaviorsRegistrar;
import fr.en0ri4n.craftcreator.utils.Identifier;

/**
 * Register core block-entity definitions.
 * This class executes a static registration so the definitions are available
 * when platform adapters run InitManager/BlockEntity registration.
 * <p>
 * Make sure this class is referenced during mod startup (for example from
 * CraftCreatorAPI.initialize).
 */
public final class DefinitionsRegistrar {

    static {
        CoreBlockEntityDefinition def = CoreBlockEntityDefinition.builder(Identifier.fromMod(BehaviorsRegistrar.CRAFTING_TABLE_RECIPE_CREATOR))
                .inventorySize(10)
                .addBehavior(BehaviorsRegistrar.CRAFTING_TABLE_RECIPE_CREATOR)
                .build();

        CoreBlockEntityManager.get().registerDefinition(def);
    }

    private DefinitionsRegistrar() {}
}