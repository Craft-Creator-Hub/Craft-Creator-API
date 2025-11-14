package fr.en0ri4n.craftcreator.api.recipe;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeTypeKey {
    private final Identifier id; // e.g. "minecraft:smelting"
}