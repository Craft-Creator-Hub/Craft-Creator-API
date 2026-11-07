package fr.en0ri4n.craftcreator.recipe.serialize;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreator;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RecipeSerializerRegistry
{
    private static final RecipeSerializerRegistry INSTANCE = new RecipeSerializerRegistry();
    public static RecipeSerializerRegistry get() { return INSTANCE; }

    /** Serializers by recipe creator id and by recipe type id */
    private final Map<String, RecipeSerializer> idToSerializerMap = new HashMap<>();

    /**
     * @param id           the recipe creator id
     * @param recipeTypes  the recipe types it makes
     */
    public void register(Identifier id, List<Identifier> recipeTypes, RecipeSerializer serializer)
    {
        idToSerializerMap.put(id.toString(), serializer);
        recipeTypes.forEach(typeId -> idToSerializerMap.putIfAbsent(typeId.toString(), serializer));
    }

    /**
     * @param recipeTypeId a recipe type (e.g. "minecraft:smelting") or a recipe creator id
     */
    public RecipeSerializer getByRecipeTypeId(Identifier recipeTypeId)
    {
        return idToSerializerMap.get(recipeTypeId.toString());
    }

    public static void registerAll()
    {
        for(RecipeCreator<?> creator : RecipeCreators.ALL_RECIPE_CREATORS)
        {
            CraftCreatorAPI.LOGGER.info("Registering recipe serializer for recipe creator: " + creator.getId());
            get().register(creator.getId(), creator.getRecipeTypeIds(), creator.getSerializer());
        }
    }
}
