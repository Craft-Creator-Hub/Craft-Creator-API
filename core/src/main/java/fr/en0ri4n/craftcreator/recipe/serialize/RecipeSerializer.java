package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RecipeSerializer
{
    private static final Identifier AIR = Identifier.from("minecraft:air");

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

    /**
     * A block entity can be turned into a recipe only if it has at least one input and a non-empty output.
     */
    public boolean isBlockDataValid(CoreBlockEntity cbe)
    {
        if(!(cbe.getBehavior() instanceof RecipeCreatorBlockEntityBehavior)) return false;

        List<RecipeEntry> entries = processBlockEntity(cbe);
        boolean hasInput = entries.stream().anyMatch(entry -> entry.getType() == RecipeEntry.EntryType.INPUT && !isEmpty(entry));
        boolean hasOutput = entries.stream().anyMatch(entry -> entry.getType() == RecipeEntry.EntryType.OUTPUT && !isEmpty(entry));
        return hasInput && hasOutput;
    }

    private static boolean isEmpty(RecipeEntry entry)
    {
        return entry.getCount() <= 0 || AIR.toString().equals(entry.getId().toString());
    }

    protected static RecipeEntry findFirst(List<RecipeEntry> entries, RecipeEntry.EntryType type)
    {
        return entries.stream()
                .filter(entry -> entry.getType() == type)
                .findFirst()
                .orElse(RecipeEntry.EMPTY);
    }

    /* ---------------- internal helpers ---------------- */

    /**
     * Add the content-based "id" and a readable "name" to a finished recipe json.
     * Identical recipes get the same id, which is how exporters detect duplicates.
     */
    protected static JsonObject withIdAndName(JsonObject recipe, String namePrefix)
    {
        String hash = generateHash(recipe);
        recipe.addProperty("id", hash);
        recipe.addProperty("name", namePrefix + "_" + hash.substring(0, 5));
        return recipe;
    }

    public static String generateHash(JsonObject json)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(json.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for(byte b : hashBytes)
                hexString.append(String.format("%02x", b));
            return hexString.toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            // MD5 is required by every JVM, this can't happen
            throw new IllegalStateException(e);
        }
    }

    protected static boolean usesNewIngredientFormat()
    {
        return SupportedVersion.isGreaterOrEquals(SupportedVersion.V1_21_2);
    }

    protected String getIdIdentifier()
    {
        return usesNewIngredientFormat() ? "id" : "item";
    }

    /**
     * Parse an ingredient in any of the supported formats:
     * <ul>
     *     <li>1.21.2+: a string, tags begin with '#' ("#minecraft:planks")</li>
     *     <li>before: {"item":"mod:item","count":n} or {"tag":"mod:tag","count":n}</li>
     * </ul>
     *
     * @return the parsed RecipeEntry, or null if parsing failed
     */
    protected RecipeEntry parseIngredient(JsonElement element)
    {
        if(element == null) return null;
        if(element.isJsonPrimitive()) return parseIngredientString(element.getAsString());
        if(element.isJsonObject()) return parseIngredientObject(element.getAsJsonObject());

        CraftCreatorAPI.LOGGER.warn("Unsupported ingredient format: " + element);
        return null;
    }

    /**
     * Supports forms like {"item":"mod:item","count":n} or {"tag":"mod:tag","count":n}
     */
    protected RecipeEntry parseIngredientObject(JsonObject obj)
    {
        if(obj == null) return null;

        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;

        try
        {
            // tag takes precedence
            if(obj.has("tag")) return RecipeEntry.itemTag(Identifier.from(obj.get("tag").getAsString()), count);
            if(obj.has("item")) return RecipeEntry.item(Identifier.from(obj.get("item").getAsString()), count);
        }
        catch(Exception e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to parse ingredient: " + obj, e);
            return null;
        }

        // fallback: try single object with "ingredient" style (some datapack formats)
        if(obj.has("ingredient") && obj.get("ingredient").isJsonObject())
            return parseIngredientObject(obj.getAsJsonObject("ingredient"));

        CraftCreatorAPI.LOGGER.warn("Ingredient object missing 'item' or 'tag': " + obj);
        return null;
    }

    private RecipeEntry parseIngredientString(String str)
    {
        if(str == null || str.isEmpty()) return null;

        try
        {
            return str.startsWith("#")
                    ? RecipeEntry.itemTag(Identifier.from(str.substring(1)), 1)
                    : RecipeEntry.item(Identifier.from(str), 1);
        }
        catch(Exception e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to parse ingredient: " + str, e);
            return null;
        }
    }

    /**
     * Parse result object which usually contains "item" (or "id" in 1.21.2+) and optional "count".
     */
    protected RecipeEntry parseResultObject(JsonObject obj)
    {
        if(obj == null || !obj.has(getIdIdentifier())) return null;

        try
        {
            Identifier itemId = Identifier.from(obj.get(getIdIdentifier()).getAsString());
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            return RecipeEntry.item(itemId, count);
        }
        catch(Exception e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to parse result: " + obj, e);
            return null;
        }
    }

    /**
     * Serialize an ingredient without count, in the format of the current Minecraft version.
     */
    public static JsonElement ingredientJson(RecipeEntry entry)
    {
        if(usesNewIngredientFormat())
            return new JsonPrimitive((entry.isTag() ? "#" : "") + entry.getId());

        JsonObject obj = new JsonObject();
        obj.addProperty(entry.isTag() ? "tag" : "item", entry.getId().toString());
        return obj;
    }

    protected JsonObject resultJson(RecipeEntry entry)
    {
        JsonObject result = new JsonObject();
        result.addProperty(getIdIdentifier(), entry.getId().toString());
        result.addProperty("count", entry.getCount());
        return result;
    }

    protected record PatternAndKey(JsonArray pattern, JsonObject key) {}

    /**
     * Build the "pattern" and "key" of a shaped recipe.
     * Empty rows and columns on the borders are trimmed, inner ones are kept so the shape stays the same.
     */
    protected static PatternAndKey buildPatternAndKey(CraftingGrid grid)
    {
        // Map each distinct ingredient to a single character symbol
        Map<RecipeEntryKey, Character> symbolMap = new HashMap<>();
        JsonObject key = new JsonObject();
        char nextSymbol = 'A';

        int minX = grid.getWidth(), minY = grid.getHeight(), maxX = -1, maxY = -1;
        char[][] rows = new char[grid.getHeight()][grid.getWidth()];

        for(int y = 0; y < grid.getHeight(); y++)
        {
            for(int x = 0; x < grid.getWidth(); x++)
            {
                RecipeEntry entry = grid.get(x, y);
                if(entry == null)
                {
                    rows[y][x] = ' ';
                    continue;
                }

                RecipeEntryKey entryKey = new RecipeEntryKey(entry.isTag(), entry.getId().toString());
                if(!symbolMap.containsKey(entryKey))
                {
                    symbolMap.put(entryKey, nextSymbol);
                    key.add(String.valueOf(nextSymbol), ingredientJson(entry));
                    nextSymbol++;
                }
                rows[y][x] = symbolMap.get(entryKey);

                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }

        JsonArray pattern = new JsonArray();
        for(int y = minY; y <= maxY; y++)
            pattern.add(new String(rows[y], minX, maxX - minX + 1));

        return new PatternAndKey(pattern, key);
    }

    private record RecipeEntryKey(boolean isTag, String id) {}
}
