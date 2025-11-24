package fr.en0ri4n.craftcreator.api.mod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupportedRecipeType
{
    CRAFTING_TABLE_SHAPED("minecraft:crafting_shaped"),
    CRAFTING_TABLE_SHAPELESS("minecraft:crafting_shapeless"),
    FURNACE("craftcreator:furnace"),
    STONECUTTER("minecraft:stonecutting"),
    CAMPFIRE("minecraft:campfire_cooking"),
    SMITHING_TABLE("minecraft:smithing"),
    GRINDSTONE("minecraft:grinding");

    private final String id;
}
