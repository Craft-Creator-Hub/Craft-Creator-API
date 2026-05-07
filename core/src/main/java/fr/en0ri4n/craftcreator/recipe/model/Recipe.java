package fr.en0ri4n.craftcreator.recipe.model;

import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public static final Recipe EMPTY = new Recipe(Identifier.fromMod("empty"), "empty", Identifier.fromMod("no_type"), List.of(), List.of(), RecipeInfos.create());

    /**
     * The datapack recipe uuid, e.g. "123e4567-e89b-12d3-a456-426614174000"
     */
    private final Identifier id;

    /**
     * The recipe name, e.g. "iron_ingot_from_smelting"
     */
    private final String name;

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

    public List<RecipeEntry> getEntries()
    {
        return Stream.concat(inputs.stream(), outputs.stream()).collect(Collectors.toList());
    }
}