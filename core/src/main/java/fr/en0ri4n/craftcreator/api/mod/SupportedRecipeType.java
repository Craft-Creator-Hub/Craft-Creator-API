package fr.en0ri4n.craftcreator.api.mod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupportedRecipeType
{
    /**
     * Will not be used as type, see {@link fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CraftingTableRCBehavior.CraftingType} for more details.
     */
    CRAFTING_SHAPED("minecraft:crafting_shaped"),
    CRAFTING_SHAPELESS("minecraft:crafting_shapeless"),

    /**
     * Will not be used as type, see {@link fr.en0ri4n.craftcreator.impl.blockentity.behaviors.FurnaceRCBehavior.FurnaceType} for more details.
     */
    FURNACE("minecraft:smelting"),
    BLAST_FURNACE("minecraft:blasting"),
    SMOKER("minecraft:smoking"),

    STONECUTTER("minecraft:stonecutting"),
    CAMPFIRE("minecraft:campfire_cooking"),
    SMITHING_TABLE("minecraft:smithing"),
    GRINDSTONE("minecraft:grinding");

    private final String id;
}
