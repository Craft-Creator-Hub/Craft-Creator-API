package fr.en0ri4n.craftcreator.api.recipe.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.recipe.model.CraftingGrid;
import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CraftingTableRecipeSerializer
{
    /**
     * Build a shaped crafting recipe JSON (minecraft:crafting_shaped).
     */
    public static JsonObject shaped(Identifier resultId, int resultCount, CraftingGrid grid)
    {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:crafting_shaped");

        PatternAndKey pk = buildPatternAndKey(grid);
        root.add("pattern", pk.pattern);
        root.add("key", pk.key);

        // result
        JsonObject result = new JsonObject();
        result.addProperty("item", resultId.toString());
        result.addProperty("count", resultCount);
        root.add("result", result);

        return root;
    }

    /**
     * Build a shapeless crafting recipe JSON (minecraft:crafting_shapeless).
     */
    public static JsonObject shapeless(Identifier resultId, int resultCount, RecipeEntry.MultiInput inputs)
    {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:crafting_shapeless");

        JsonArray ingredients = new JsonArray();
        for(RecipeEntry entry : inputs.getEntries())
        {
            ingredients.add(singletonItemJson(entry));
        }
        root.add("ingredients", ingredients);

        JsonObject result = new JsonObject();
        result.addProperty("item", resultId.toString());
        result.addProperty("count", resultCount);
        root.add("result", result);

        return root;
    }

    /* ---------------- internal helpers ---------------- */

    private static JsonObject singletonItemJson(RecipeEntry entry)
    {
        JsonObject obj = new JsonObject();
        String key = entry.isTag() ? "tag" : "item";
        obj.addProperty(key, entry.getId().toString());
        return obj;
    }

    private static class PatternAndKey
    {
        final JsonArray pattern;
        final JsonObject key;

        PatternAndKey(JsonArray pattern, JsonObject key)
        {
            this.pattern = pattern;
            this.key = key;
        }
    }

    private static PatternAndKey buildPatternAndKey(CraftingGrid grid)
    {
        // Map each distinct ingredient to a single character symbol
        Map<String, Character> symbolMap = new HashMap<>();
        char nextSymbol = 'A';

        // Build a char matrix
        char[][] matrix = new char[grid.getHeight()][grid.getWidth()];
        for(int y = 0; y < grid.getHeight(); y++)
        {
            for(int x = 0; x < grid.getWidth(); x++)
            {
                RecipeEntry entry = grid.get(x, y);
                if(entry == null)
                {
                    matrix[y][x] = ' ';
                    continue;
                }
                String key = (entry.isTag() ? "#" : "") + entry.getId().toString();
                if(!symbolMap.containsKey(key))
                {
                    symbolMap.put(key, nextSymbol++);
                }
                matrix[y][x] = symbolMap.get(key);
            }
        }

        // Build pattern array
        JsonArray pattern = new JsonArray();
        for(int y = 0; y < grid.getHeight(); y++)
        {
            pattern.add(new String(matrix[y]));
        }

        // Build key object
        JsonObject key = getJsonObject(symbolMap);

        return new PatternAndKey(pattern, key);
    }

    private static JsonObject getJsonObject(Map<String, Character> symbolMap)
    {
        JsonObject key = new JsonObject();
        for(Map.Entry<String, Character> e : symbolMap.entrySet())
        {
            String ingredientKey = e.getValue().toString();
            String valueId = e.getKey();
            JsonObject ing = new JsonObject();
            if(valueId.startsWith("#"))
            {
                ing.addProperty("tag", valueId.substring(1));
            }
            else
            {
                ing.addProperty("item", valueId);
            }
            key.add(ingredientKey, ing);
        }
        return key;
    }
}