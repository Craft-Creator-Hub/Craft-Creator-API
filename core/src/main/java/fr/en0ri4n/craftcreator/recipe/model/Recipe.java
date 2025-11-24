package fr.en0ri4n.craftcreator.recipe.model;

import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Recipe model used by core for management.
 * <p/>
 * This class is only used for "representation", they are directly deserialized from JSON,
 * but never serialized back. Serialization is handled by each RecipeSerializer implementation using
 * block entities & behaviors data.
 */
@Getter
@AllArgsConstructor
public class Recipe
{
    public static final Recipe EMPTY = new Recipe(Identifier.fromMod("empty"), Identifier.fromMod("no_type"), List.of(), List.of(), RecipeInfos.create());

    /**
     * The datapack recipe id, e.g. "craftcreator:iron_from_custom"
     */
    private final Identifier id;

    /**
     * The recipe type, e.g. "minecraft:smelting", "botania:mana_infusion"
     */
    private final Identifier type;

    /**
     * Inputs (items/fluids/blocks)
     */
    private final List<RecipeEntry> inputs;

    /**
     * Outputs (items/fluids/blocks)
     */
    private final List<RecipeEntry> outputs;

    /**
     * Additional parameters (time, energy, shapeless, etc.)
     */
    private final RecipeInfos infos;
}