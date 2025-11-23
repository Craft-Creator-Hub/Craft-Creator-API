package fr.en0ri4n.craftcreator.recipe.serialize;

import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeTypes;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RecipeSerializerRegistry
{
    private static final RecipeSerializerRegistry INSTANCE = new RecipeSerializerRegistry();
    public static RecipeSerializerRegistry get() { return INSTANCE; }

    private final Map<SupportedRecipeTypes, RecipeSerializer> serializers = new HashMap<>();

    public void register(SupportedRecipeTypes recipeType, RecipeSerializer serializer)
    {
        serializers.put(recipeType, serializer);
    }

    public RecipeSerializer get(SupportedRecipeTypes recipeType)
    {
        return serializers.get(recipeType);
    }

    public RecipeSerializer get(Identifier recipeTypeId)
    {
        for(SupportedRecipeTypes type : serializers.keySet())
        {
            if(type.getId().equals(recipeTypeId.toString()))
                return serializers.get(type);
        }
        return null;
    }

    public static void register()
    {
        get().register(SupportedRecipeTypes.CRAFTING_TABLE_SHAPED, new CraftingTableRecipeSerializer());
    }
}
