package fr.en0ri4n.craftcreator.api.mod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupportedRecipeTypes
{
    CRAFTING_TABLE_SHAPED("minecraft:crafting_shaped"),
    CRAFTING_TABLE_SHAPELESS("minecraft:crafting_shapeless"),
    FURNACE("minecraft:smelting"),
    BLAST_FURNACE("minecraft:blasting"),
    SMOKER("minecraft:smoking"),
    STONECUTTER("minecraft:stonecutting"),
    CAMPFIRE("minecraft:campfire_cooking"),
    SMITHING_TABLE("minecraft:smithing"),
    LOOM("minecraft:loom"),
    CARTOGRAPHY_TABLE("minecraft:cartography"),
    GRINDSTONE("minecraft:grinding"),
    BREWING_STAND("minecraft:brewing");

    private final String id;
}
