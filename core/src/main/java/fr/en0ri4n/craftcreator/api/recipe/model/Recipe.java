package fr.en0ri4n.craftcreator.api.recipe.model;

import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Generic, loader-agnostic recipe model used by core.
 * Represents one datapack recipe.
 */
@Getter
@AllArgsConstructor
public class Recipe {

    /** The datapack recipe id, e.g. "craftcreator:iron_from_custom" */
    private final Identifier id;

    /** The recipe type, e.g. "minecraft:smelting", "botania:mana_infusion" */
    private final Identifier type;

    /** Inputs (items/fluids/blocks) as generic entries; see your existing RecipeEntry hierarchy. */
    private final List<RecipeEntryWrapper> inputs;

    /** Outputs as generic entries. */
    private final List<RecipeEntryWrapper> outputs;

    /** Additional parameters (time, energy, shapeless, etc.) */
    private final RecipeInfos infos;
}