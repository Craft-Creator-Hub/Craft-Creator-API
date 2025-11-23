package fr.en0ri4n.craftcreator.serialize;

import fr.en0ri4n.craftcreator.recipe.serialize.CraftingTableRecipeSerializer;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeInfosSerializer;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerializerRegistry {

    private static final Map<Class<?>, JsonSerializer<?>> BY_CLASS = new HashMap<>();

    public static <T> void register(Class<T> type, JsonSerializer<T> serializer) {
        BY_CLASS.put(type, serializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> JsonSerializer<T> get(Class<T> type) {
        return (JsonSerializer<T>) BY_CLASS.get(type);
    }

    public static void registerAll()
    {
        register(RecipeInfos.class, new RecipeInfosSerializer());
//        register(CraftingTableRecipeSerializer.class, new CraftingTableRecipeSerializer());
    }
}