package fr.en0ri4n.craftcreator.recipe.slot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializer;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * What a {@link SlotRecipeType.Writer} can read from the recipe creator: slot contents and typed values,
 * plus helpers producing the usual json shapes.
 */
public class SlotRecipeContext
{
    @Getter
    private final SlotRecipeType type;
    /** Recipe type being made, among the type's variants. */
    @Getter
    private final SlotRecipeType.Variant variant;
    /** Entry of each slot, by slot index; null for empty slots. */
    private final RecipeEntry[] entries;
    private final List<CoreItemStack> inventory;
    private final Map<String, String> params;

    public SlotRecipeContext(SlotRecipeType type, SlotRecipeType.Variant variant, List<CoreItemStack> inventory, Map<Integer, Identifier> taggedSlots, Map<String, String> params)
    {
        this.type = type;
        this.variant = variant;
        this.params = params;
        this.entries = new RecipeEntry[type.getSlots().size()];
        this.inventory = inventory;

        for(int i = 0; i < entries.length && i < inventory.size(); i++)
            entries[i] = toEntry(type.getSlots().get(i), i, inventory.get(i), taggedSlots.get(i));
    }

    private static RecipeEntry toEntry(SlotSpec slot, int index, CoreItemStack stack, Identifier tag)
    {
        if(stack == null || stack.isEmpty()) return null;

        RecipeEntry entry = switch(slot.kind())
        {
            case ITEM -> tag != null && !slot.output() ? RecipeEntry.itemTag(tag, stack.getCount()) : RecipeEntry.item(stack.getItemId(), stack.getCount());
            case BLOCK -> RecipeEntry.block(stack.getItemId(), stack.getCount());
            case FLUID ->
            {
                Identifier fluid = CraftCreatorAPI.get().getPlatform().getItemStackAdapter().getContainedFluid(stack);
                yield fluid == null ? null : RecipeEntry.fluid(fluid, stack.getCount());
            }
        };

        if(entry == null) return null;
        entry.setType(slot.output() ? RecipeEntry.EntryType.OUTPUT : RecipeEntry.EntryType.INPUT);
        entry.setSlot(index);
        return entry;
    }

    /* ---------------- slots ---------------- */

    /**
     * @return the entry in the slot with this name, or null if empty
     */
    public RecipeEntry get(String slotName)
    {
        int index = type.indexOf(slotName);
        return index < 0 ? null : entries[index];
    }

    public boolean has(String slotName)
    {
        return get(slotName) != null;
    }

    /**
     * @return the non-empty entries of slots added with {@code inputs(prefix, ...)} or {@code outputs(prefix, ...)}, in order
     */
    public List<RecipeEntry> all(String prefix)
    {
        List<RecipeEntry> result = new ArrayList<>();
        for(int i = 0; i < entries.length; i++)
            if(entries[i] != null && type.getSlots().get(i).name().startsWith(prefix + "_"))
                result.add(entries[i]);
        return result;
    }

    /**
     * @return every non-empty slot entry
     */
    public List<RecipeEntry> entries()
    {
        List<RecipeEntry> result = new ArrayList<>();
        for(RecipeEntry entry : entries)
            if(entry != null) result.add(entry);
        return result;
    }

    /**
     * @return the fluid contained in the item of this slot (e.g. a bucket), whatever the slot's kind, or null
     */
    public RecipeEntry fluidOf(String slotName)
    {
        int index = type.indexOf(slotName);
        if(index < 0 || index >= inventory.size() || inventory.get(index) == null || inventory.get(index).isEmpty()) return null;
        Identifier fluid = CraftCreatorAPI.get().getPlatform().getItemStackAdapter().getContainedFluid(inventory.get(index));
        return fluid == null ? null : RecipeEntry.fluid(fluid, 1);
    }

    /* ---------------- values ---------------- */

    public String param(String key)
    {
        String value = params.get(key);
        if(value != null && !value.isBlank()) return value.trim();
        return type.getParam(key).map(ParamSpec::defaultValue).orElse("");
    }

    public int paramInt(String key)
    {
        try
        {
            return Integer.parseInt(param(key));
        }
        catch(NumberFormatException e)
        {
            return type.getParam(key).map(p -> Integer.parseInt(p.defaultValue())).orElse(0);
        }
    }

    public double paramDouble(String key)
    {
        try
        {
            return Double.parseDouble(param(key));
        }
        catch(NumberFormatException e)
        {
            return type.getParam(key).map(p -> Double.parseDouble(p.defaultValue())).orElse(0D);
        }
    }

    public boolean paramBool(String key)
    {
        return Boolean.parseBoolean(param(key));
    }

    /**
     * Add the value as an int property only if it's greater than zero (zero means "use the mod's default").
     */
    public void addIfPositive(JsonObject out, String jsonKey, String paramKey)
    {
        int value = paramInt(paramKey);
        if(value > 0) out.addProperty(jsonKey, value);
    }

    /* ---------------- json helpers ---------------- */

    /**
     * {"item": id} or {"tag": id}, without count.
     */
    public JsonElement ingredient(RecipeEntry entry)
    {
        return RecipeSerializer.ingredientJson(entry);
    }

    /**
     * {"item": id, "count": n} or {"tag": id, "count": n}; count only written when greater than one.
     */
    public JsonObject countedIngredient(RecipeEntry entry)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty(entry.isTag() ? "tag" : "item", entry.getId().toString());
        if(entry.getCount() > 1) obj.addProperty("count", entry.getCount());
        return obj;
    }

    public JsonArray ingredients(List<RecipeEntry> entries)
    {
        JsonArray array = new JsonArray();
        entries.forEach(entry -> array.add(ingredient(entry)));
        return array;
    }

    public JsonArray countedIngredients(List<RecipeEntry> entries)
    {
        JsonArray array = new JsonArray();
        entries.forEach(entry -> array.add(countedIngredient(entry)));
        return array;
    }

    /**
     * {"item": id, "count": n}; count only written when greater than one.
     */
    public JsonObject item(RecipeEntry entry)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("item", entry.getId().toString());
        if(entry.getCount() > 1) obj.addProperty("count", entry.getCount());
        return obj;
    }

    public JsonArray items(List<RecipeEntry> entries)
    {
        JsonArray array = new JsonArray();
        entries.forEach(entry -> array.add(item(entry)));
        return array;
    }

    /**
     * {"item": id, "count": n} with the item key of the current Minecraft version ("id" in 1.21.2+); count always written.
     */
    public JsonObject result(RecipeEntry entry)
    {
        return RecipeSerializer.resultJson(entry);
    }

    /**
     * {"fluid": id, "amount": amount}
     */
    public JsonObject fluid(RecipeEntry entry, int amount)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("fluid", entry.getId().toString());
        obj.addProperty("amount", amount);
        return obj;
    }

    /**
     * {"type": "block", "block": id}, the block matcher format used by Botania.
     */
    public JsonObject blockMatcher(RecipeEntry entry)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "block");
        obj.addProperty("block", entry.getId().toString());
        return obj;
    }
}
