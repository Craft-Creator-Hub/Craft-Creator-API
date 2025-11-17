package fr.en0ri4n.craftcreator.api.blockentity.behaviors;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import fr.en0ri4n.craftcreator.utils.Identifier;

/**
 * Register simple core behaviors. This class performs registration in a static block,
 * so make sure Core module is initialized early (call the class or reference it from core init).
 */
public final class BehaviorsRegistrar {

    public static final Identifier CRAFTING_TABLE_RECIPE_CREATOR = Identifier.fromMod("crafting_table_recipe_creator");

    static {
        CoreBlockEntityManager.get().registerBehavior(CRAFTING_TABLE_RECIPE_CREATOR, CraftingTableRecipeCreatorBehavior::new);
    }

    private BehaviorsRegistrar() {}
}