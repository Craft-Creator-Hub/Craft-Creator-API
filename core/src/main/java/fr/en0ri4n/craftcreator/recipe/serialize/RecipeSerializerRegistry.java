package fr.en0ri4n.craftcreator.recipe.serialize;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeType;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RecipeSerializerRegistry
{
    private static final RecipeSerializerRegistry INSTANCE = new RecipeSerializerRegistry();
    public static RecipeSerializerRegistry get() { return INSTANCE; }

    private final Map<String, RecipeSerializer> idToSerializerMap = new HashMap<>();
    private final Map<SupportedRecipeType, RecipeSerializer> recipeTypeToSerializerMap = new HashMap<>();

    public void register(Identifier id, SupportedRecipeType recipeType, RecipeSerializer serializer)
    {
        idToSerializerMap.put(id.toString(), serializer);
        recipeTypeToSerializerMap.put(recipeType, serializer);
    }

    public RecipeSerializer get(SupportedRecipeType recipeType)
    {
        return recipeTypeToSerializerMap.get(recipeType);
    }

    public RecipeSerializer get(Identifier recipeTypeId)
    {
        return idToSerializerMap.get(recipeTypeId.toString());
    }

    public static void registerAll()
    {
        get().register(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR, SupportedRecipeType.CRAFTING_TABLE_SHAPED, new CraftingTableRecipeSerializer());
        get().register(RecipeCreators.FURNACE_RECIPE_CREATOR, SupportedRecipeType.FURNACE, new FurnaceRecipeSerializer());
    }
}
