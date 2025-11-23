package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.TaggableSlotsBlockEntityBehavior;
import fr.en0ri4n.craftcreator.recipe.model.CraftingGrid;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.serialize.JsonSerializer;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RecipeSerializer implements JsonSerializer<CoreBlockEntity>
{
    protected abstract void processInventory(List<RecipeEntry> entries, List<CoreItemStack> inventory, Map<Integer, Identifier> taggedSlots);

    public abstract Recipe deserializeToRecipe(JsonObject in);

    public List<RecipeEntry> processBlockEntity(CoreBlockEntity cbe)
    {
        TaggableSlotsBlockEntityBehavior behavior = (TaggableSlotsBlockEntityBehavior) cbe.getBehavior();
        List<RecipeEntry> entries = new ArrayList<>();
        processInventory(entries, cbe.getInventory(), behavior.getTaggedSlots());
        return entries;
    }

    public boolean canSerializeRecipe(CoreBlockEntity cbe)
    {
        TaggableSlotsBlockEntityBehavior behavior = (TaggableSlotsBlockEntityBehavior) cbe.getBehavior();
        return behavior != null && !behavior.getTaggedSlots().isEmpty();
    }

    /* ---------------- internal helpers ---------------- */

    /**
     * Helper to parse a single ingredient JSON object into a RecipeEntry.
     * Supports forms like {"item":"mod:item","count":n} or {"tag":"mod:tag","count":n}
     */
    protected RecipeEntry parseIngredientObject(JsonObject obj)
    {
        if(obj == null) return null;

        // tag takes precedence
        if(obj.has("tag"))
        {
            try
            {
                String tagStr = obj.get("tag").getAsString();
                Identifier tagId = Identifier.from(tagStr);
                int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
                RecipeEntry e = RecipeEntry.itemTag(tagId, count);
                return e;
            }
            catch(Exception ignored)
            {
            }
        }

        if(obj.has("item"))
        {
            try
            {
                String itemStr = obj.get("item").getAsString();
                Identifier itemId = Identifier.from(itemStr);
                int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
                RecipeEntry e = RecipeEntry.item(itemId, count);
                return e;
            }
            catch(Exception ignored)
            {
            }
        }

        // fallback: try single object with "ingredient" style (some datapack formats)
        if(obj.has("ingredient") && obj.get("ingredient").isJsonObject())
        {
            return parseIngredientObject(obj.getAsJsonObject("ingredient"));
        }

        return null;
    }

    /**
     * Parse result object which usually contains "item" and optional "count".
     */
    protected RecipeEntry parseResultObject(JsonObject obj)
    {
        if(obj == null) return null;
        if(!obj.has("item")) return null;
        try
        {
            Identifier itemId = Identifier.from(obj.get("item").getAsString());
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            return RecipeEntry.item(itemId, count);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public static JsonObject singletonItemJson(RecipeEntry entry)
    {
        JsonObject obj = new JsonObject();
        String key = entry.isTag() ? "tag" : "item";
        obj.addProperty(key, entry.getId().toString());
        return obj;
    }

    protected static class PatternAndKey
    {
        final JsonArray pattern;
        final JsonObject key;

        PatternAndKey(JsonArray pattern, JsonObject key)
        {
            this.pattern = pattern;
            this.key = key;
        }
    }

    protected static PatternAndKey buildPatternAndKey(CraftingGrid grid)
    {
        // Map each distinct ingredient to a single character symbol
        Map<String, Character> symbolMap = new HashMap<>();
        char nextSymbol = 'A';

        // Build a char matrix
        char[][] matrix = new char[grid.getWidth()][grid.getHeight()];
        for(int y = 0; y < grid.getHeight(); y++)
        {
            for(int x = 0; x < grid.getWidth(); x++)
            {
                RecipeEntry entry = grid.get(x, y);
                if(entry == null)
                {
                    matrix[x][y] = ' ';
                    continue;
                }
                String key = (entry.isTag() ? "#" : "") + entry.getId().toString();
                if(!symbolMap.containsKey(key))
                {
                    symbolMap.put(key, nextSymbol++);
                }
                matrix[x][y] = symbolMap.get(key);
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

    protected static JsonObject getJsonObject(Map<String, Character> symbolMap)
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
