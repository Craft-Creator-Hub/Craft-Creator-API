package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.mod.SupportedVersion;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.TaggableSlotsBlockEntityBehavior;
import fr.en0ri4n.craftcreator.recipe.model.CraftingGrid;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public abstract class RecipeSerializer
{
    public abstract JsonObject serialize(CoreBlockEntity value);

    protected abstract void processInventory(List<RecipeEntry> entries, List<CoreItemStack> inventory, Map<Integer, Identifier> taggedSlots);

    public abstract Recipe deserializeToRecipe(JsonObject in);

    public List<RecipeEntry> processBlockEntity(CoreBlockEntity cbe)
    {
        TaggableSlotsBlockEntityBehavior behavior = (TaggableSlotsBlockEntityBehavior) cbe.getBehavior();
        List<RecipeEntry> entries = new ArrayList<>();
        processInventory(entries, cbe.getInventory(), behavior.getTaggedSlots());
        return entries;
    }

    public boolean isBlockDataValid(CoreBlockEntity cbe)
    {
        RecipeCreatorBlockEntityBehavior behavior = (RecipeCreatorBlockEntityBehavior) cbe.getBehavior();
        return behavior != null;
    }

    /* ---------------- internal helpers ---------------- */

    /**
     * Create base recipe JSON object with type, id and name.
     */
    protected JsonObject createBaseRecipeObject(Identifier type, String name)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", type.toString());
        obj.addProperty("id", UUID.randomUUID().toString());
        obj.addProperty("name", name);
        return obj;
    }

    public static String generateHash(JsonObject json)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(json.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e)
        {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

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
                return RecipeEntry.itemTag(tagId, count);
            }
            catch(Exception e)
            {
                CraftCreatorAPI.LOGGER.error("Failed to parse ingredient tag: " + obj, e);
            }
        }

        if(obj.has("item"))
        {
            try
            {
                String itemStr = obj.get("item").getAsString();
                Identifier itemId = Identifier.from(itemStr);
                int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
                return RecipeEntry.item(itemId, count);
            }
            catch(Exception e)
            {
                CraftCreatorAPI.LOGGER.error("Failed to parse ingredient item: " + obj, e);
            }
        }

        // fallback: try single object with "ingredient" style (some datapack formats)
        if(obj.has("ingredient") && obj.get("ingredient").isJsonObject())
        {
            return parseIngredientObject(obj.getAsJsonObject("ingredient"));
        }

        CraftCreatorAPI.LOGGER.warn("Ingredient object missing 'item' or 'tag': " + obj);
        return null;
    }

    protected String getIdIdentifier()
    {
        return SupportedVersion.isGreaterOrEquals(SupportedVersion.V1_21_2) ? "id" : "item";
    }

    /**
     * Parse ingredient object for Minecraft versions 1.21.2 and above.
     * In these versions, ingredients are a simple array of ingredient objects, and tags begins with '#'.
     * @param obj the ingredient string
     * @return the parsed RecipeEntry, or null if parsing failed
     */
    protected RecipeEntry parseIngredientNewVersion(String obj)
    {
        if(obj == null || obj.isEmpty()) return null;

        try
        {
            if(obj.startsWith("#"))
            {
                String tagStr = obj.substring(1);
                Identifier tagId = Identifier.from(tagStr);
                return RecipeEntry.itemTag(tagId, 1);
            }
            else
            {
                Identifier itemId = Identifier.from(obj);
                return RecipeEntry.item(itemId, 1);
            }
        }
        catch(Exception ignored)
        {
            CraftCreatorAPI.LOGGER.error("Failed to parse ingredient: " + obj);
        }

        return null;
    }

    /**
     * Parse result object which usually contains "item" and optional "count".
     */
    protected RecipeEntry parseResultObject(JsonObject obj)
    {
        if(obj == null) return null;
        if(!obj.has(getIdIdentifier())) return null;
        try
        {
            Identifier itemId = Identifier.from(obj.get(getIdIdentifier()).getAsString());
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

        // Clear empty rows and columns
        for(int y = 0; y < grid.getHeight(); y++)
        {
            boolean emptyRow = true;
            for(int x = 0; x < grid.getWidth(); x++)
            {
                if(matrix[x][y] != ' ')
                {
                    emptyRow = false;
                    break;
                }
            }
            if(emptyRow)
            {
                for(int x = 0; x < grid.getWidth(); x++)
                {
                    matrix[x][y] = '\0';
                }
            }
        }
        for(int x = 0; x < grid.getWidth(); x++)
        {
            boolean emptyCol = true;
            for(int y = 0; y < grid.getHeight(); y++)
            {
                if(matrix[x][y] != ' ')
                {
                    emptyCol = false;
                    break;
                }
            }
            if(emptyCol)
            {
                for(int y = 0; y < grid.getHeight(); y++)
                {
                    matrix[x][y] = '\0';
                }
            }
        }

        // Build pattern array
        JsonArray pattern = new JsonArray();
        for(int y = 0; y < grid.getHeight(); y++)
        {
            pattern.add(new String(matrix[y]).replaceAll("\0", ""));
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

            if(SupportedVersion.isGreaterOrEquals(SupportedVersion.V1_21_2))
            {
                key.addProperty(ingredientKey, valueId);
            }
            else
            {
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
        }
        return key;
    }
}
