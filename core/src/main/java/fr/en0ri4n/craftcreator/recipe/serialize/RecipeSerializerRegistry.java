package fr.en0ri4n.craftcreator.recipe.serialize;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeType;
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

    private final Map<String, RecipeSerializer> idToSerializerMap = new HashMap<>();
    private final Map<SupportedRecipeType, RecipeSerializer> recipeTypeToSerializerMap = new HashMap<>();

    public void register(Identifier id, List<SupportedRecipeType> recipeType, RecipeSerializer serializer)
    {
        idToSerializerMap.put(id.toString(), serializer);
        for(SupportedRecipeType type : recipeType)
            recipeTypeToSerializerMap.put(type, serializer);
    }

    public RecipeSerializer getByRecipeType(SupportedRecipeType recipeType)
    {
        return recipeTypeToSerializerMap.get(recipeType);
    }

    public RecipeSerializer getByRecipeTypeId(Identifier recipeTypeId)
    {
        for(SupportedRecipeType type : recipeTypeToSerializerMap.keySet())
        {
            if(type.getId().equals(recipeTypeId.toString()))
                return getByRecipeType(type);
        }
        return idToSerializerMap.get(recipeTypeId.toString());
    }

    public static void registerAll()
    {
        for(RecipeCreator<?> creator : RecipeCreators.ALL_RECIPE_CREATORS)
        {
            CraftCreatorAPI.LOGGER.info("Registering recipe serializer for recipe creator: " + creator.getId());
            get().register(creator.getId(), creator.getRecipeTypes(), creator.getSerializer());
        }
    }
}
