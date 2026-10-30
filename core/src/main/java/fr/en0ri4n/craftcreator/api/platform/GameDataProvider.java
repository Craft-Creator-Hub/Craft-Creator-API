package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

/**
 * Read-only access to the game's content, used by screens for suggestions and recipe browsing.
 */
public interface GameDataProvider
{
    List<Identifier> getItemIds();

    List<String> getModIds();

    List<Identifier> getRecipeTypeIds();

    /**
     * Client only: every recipe the client knows (synced by the server), with their type set to their serializer id
     * (e.g. "minecraft:crafting_shaped"). Inputs of shaped recipes use 3x3 grid slot indices, other inputs use their position.
     */
    List<Recipe> getClientRecipes();
}
