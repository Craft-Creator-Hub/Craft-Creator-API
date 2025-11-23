package fr.en0ri4n.craftcreator.recipe.model;

import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Wrapper so you can evolve RecipeEntry without changing the generic Recipe signature.
 */
@Getter
@AllArgsConstructor
public class RecipeEntryWrapper {

    private final RecipeEntry entry;
}