package fr.en0ri4n.craftcreator.api.blockentity.behaviors;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;

/**
 * Register simple core behaviors. This class performs registration in a static block,
 * so make sure Core module is initialized early (call the class or reference it from core init).
 */
public final class BehaviorsRegistrar {

    public static final String CRAFTING_TABLE_RECIPE_CREATOR = "crafting_table_recipe_creator";

    static {
        // register the simple burn behavior under id "simple_burn"
        CoreBlockEntityManager.get().registerBehavior(CRAFTING_TABLE_RECIPE_CREATOR, CraftingTableRecipeCreatorBehavior::new);
    }

    private BehaviorsRegistrar() {}
}