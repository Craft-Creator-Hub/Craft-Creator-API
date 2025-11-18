package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;

/**
 * Register simple core behaviors. This class performs registration in a static block,
 * so make sure Core module is initialized early (call the class or reference it from core init).
 */
public final class CoreBlockEntityBehaviorsRegistrar
{
    public static void init() {
        CoreBlockEntityManager.get().registerBehavior(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR, CraftingTableRCBehavior::new);
    }

    private CoreBlockEntityBehaviorsRegistrar() {}
}