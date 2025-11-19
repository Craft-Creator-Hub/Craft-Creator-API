package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Register simple core behaviors. This class performs registration in a static way
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreBlockEntityBehaviorsRegistrar
{
    public static void init()
    {
        CoreBlockEntityManager.get().registerBehavior(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR, CraftingTableRCBehavior::new);
    }
}