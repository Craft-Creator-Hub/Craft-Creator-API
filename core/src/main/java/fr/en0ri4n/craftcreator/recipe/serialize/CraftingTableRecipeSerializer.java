package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeType;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CraftingTableRCBehavior;
import fr.en0ri4n.craftcreator.recipe.model.CraftingGrid;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class CraftingTableRecipeSerializer extends RecipeSerializer
{
    @Override
    protected void processInventory(List<RecipeEntry> entries, List<CoreItemStack> inventory, Map<Integer, Identifier> taggedSlots)
    {
        for(int i = 0; i < 9; i++)
        {
            if(taggedSlots.containsKey(i))
            {
                Identifier tagId = taggedSlots.get(i);
                RecipeEntry entry = RecipeEntry.itemTag(tagId, inventory.get(i).getCount());
                entry.setSlot(i);
                entries.add(entry);
            }
            else
            {
                CoreItemStack stack = inventory.get(i);
                if(!stack.isEmpty())
                {
                    RecipeEntry entry = RecipeEntry.item(stack.getItemId(), stack.getCount());
                    entry.setSlot(i);
                    entries.add(entry);
                }
            }
        }

        RecipeEntry resultEntry = RecipeEntry.item(inventory.get(9).getItemId(), inventory.get(9).getCount());
        resultEntry.setType(RecipeEntry.EntryType.OUTPUT);
        entries.add(resultEntry);
    }

    @Override
    public JsonObject serialize(CoreBlockEntity coreBlockEntity)
    {
        CraftingTableRCBehavior behavior = (CraftingTableRCBehavior) coreBlockEntity.getBehavior();

        List<RecipeEntry> entries = processBlockEntity(coreBlockEntity);

        RecipeEntry outputEntry = entries.stream()
                .filter(entry -> entry.getType() == RecipeEntry.EntryType.OUTPUT)
                .findFirst()
                .orElse(RecipeEntry.EMPTY);

        if(behavior.getCraftingType() == CraftingTableRCBehavior.CraftingType.SHAPED)
        {
            CraftingGrid grid = new CraftingGrid(3, 3);
            for(RecipeEntry entry : entries)
            {
                if(entry.getType() == RecipeEntry.EntryType.INPUT)
                {
                    int slotIndex = entry.getSlot();
                    int row = slotIndex / 3;
                    int col = slotIndex % 3;
                    grid.set(row, col, entry);
                }
            }
            return shaped(outputEntry, grid);
        }
        else if(behavior.getCraftingType() == CraftingTableRCBehavior.CraftingType.SHAPELESS)
        {
            RecipeEntry.MultiInput inputs = new RecipeEntry.MultiInput();
            entries.stream().filter(entry -> entry.getType() == RecipeEntry.EntryType.INPUT).forEach(inputs::add);
            return shapeless(outputEntry, inputs);
        }
        return null;
    }

    /**
     * Not implemented because recipes are not deserialized into block entities.
     */
    @Override
    public CoreBlockEntity deserialize(JsonObject element)
    {
        return null;
    }

    @Override
    public Recipe deserializeToRecipe(JsonObject element)
    {
        if(element == null) return Recipe.EMPTY;

        if(element.has("type"))
        {
            String type = element.get("type").getAsString();
            if(type.equals(SupportedRecipeType.CRAFTING_TABLE_SHAPED.getId()))
            {
                return deserializeShapedRecipe(element);
            }
            else if(type.equals(SupportedRecipeType.CRAFTING_TABLE_SHAPELESS.getId()))
            {
                return deserializeShapelessRecipe(element);
            }
        }
        return Recipe.EMPTY;
    }

    private Recipe deserializeShapelessRecipe(JsonObject element)
    {
        if(element == null) return Recipe.EMPTY;

        try
        {
            Identifier id = Identifier.from(element.has("id") ? element.get("id").getAsString() : "unknown");

            // parse type
            Identifier type = Identifier.from(element.get("type").getAsString());

            // parse ingredients array
            List<RecipeEntry> inputs = new ArrayList<>();
            if(element.has("ingredients") && element.get("ingredients").isJsonArray())
            {
                JsonArray ingredients = element.getAsJsonArray("ingredients");
                for(JsonElement el : ingredients)
                {
                    if(!el.isJsonObject()) continue;
                    JsonObject obj = el.getAsJsonObject();
                    RecipeEntry entry = parseIngredientObject(obj);
                    if(entry != null)
                    {
                        entry.setType(RecipeEntry.EntryType.INPUT);
                        inputs.add(entry);
                    }
                }
            }

            // parse result
            List<RecipeEntry> outputs = new ArrayList<>();
            if(element.has("result") && element.get("result").isJsonObject())
            {
                JsonObject res = element.getAsJsonObject("result");
                RecipeEntry out = parseResultObject(res);
                if(out != null)
                {
                    out.setType(RecipeEntry.EntryType.OUTPUT);
                    outputs.add(out);
                }
            }

            return new Recipe(id, type, inputs, outputs, RecipeInfos.create());
        }
        catch(Exception ex)
        {
            // on error, return EMPTY
            return Recipe.EMPTY;
        }
    }

    private Recipe deserializeShapedRecipe(JsonObject element)
    {
        if(element == null) return Recipe.EMPTY;

        try
        {
            Identifier id = Identifier.from(element.has("id") ? element.get("id").getAsString() : "unknown");

            Identifier type = Identifier.from(element.get("type").getAsString());

            // pattern is an array of strings
            JsonArray patternArray = element.has("pattern") ? element.getAsJsonArray("pattern") : null;
            JsonObject key = element.has("key") ? element.getAsJsonObject("key") : new JsonObject();

            List<RecipeEntry> inputs = new ArrayList<>();

            if(patternArray != null)
            {
                int rows = patternArray.size();
                int cols = 0;
                // determine cols from longest pattern row
                for(JsonElement e : patternArray)
                {
                    if(e.isJsonPrimitive())
                    {
                        String row = e.getAsString();
                        cols = Math.max(cols, row.length());
                    }
                }
                // iterate pattern rows and cols, map chars using key
                for(int r = 0; r < rows; r++)
                {
                    String rowStr = patternArray.get(r).getAsString();
                    for(int c = 0; c < cols; c++)
                    {
                        char ch = (c < rowStr.length() ? rowStr.charAt(c) : ' ');
                        if(ch == ' ' || ch == '\0') continue;
                        String keyChar = String.valueOf(ch);
                        if(!key.has(keyChar)) continue;
                        JsonElement mapping = key.get(keyChar);
                        if(!mapping.isJsonObject()) continue;
                        JsonObject mapObj = mapping.getAsJsonObject();
                        RecipeEntry entry = parseIngredientObject(mapObj);
                        if(entry != null)
                        {
                            entry.setType(RecipeEntry.EntryType.INPUT);
                            // set slot based on a 3x3 grid mapping: attempt to place into the 3x3 grid top-left
                            int slot = r * 3 + c; // if pattern smaller/larger than 3 this may overflow; clamp to 0..8
                            if(slot < 0) slot = 0;
                            if(slot > 8) slot = 8;
                            entry.setSlot(slot);
                            inputs.add(entry);
                        }
                    }
                }
            }

            // parse result
            List<RecipeEntry> outputs = new ArrayList<>();
            if(element.has("result") && element.get("result").isJsonObject())
            {
                JsonObject res = element.getAsJsonObject("result");
                RecipeEntry out = parseResultObject(res);
                if(out != null)
                {
                    out.setType(RecipeEntry.EntryType.OUTPUT);
                    outputs.add(out);
                }
            }

            return new Recipe(id, type, inputs, outputs, RecipeInfos.create());
        }
        catch(Exception ex)
        {
            return Recipe.EMPTY;
        }
    }

    /**
     * Build a shaped crafting recipe JSON (minecraft:crafting_shaped).
     */
    public JsonObject shaped(RecipeEntry result, CraftingGrid grid)
    {
        JsonObject root = new JsonObject();
        root.addProperty("id", result.getId().getPath() + "_shaped_recipe_" + UUID.randomUUID());
        root.addProperty("type", SupportedRecipeType.CRAFTING_TABLE_SHAPED.getId());

        PatternAndKey pk = buildPatternAndKey(grid);
        root.add("pattern", pk.pattern);
        root.add("key", pk.key);

        // result
        JsonObject jsonResult = new JsonObject();
        jsonResult.addProperty("item", result.getId().toString());
        jsonResult.addProperty("count", result.getCount());
        root.add("result", jsonResult);

        return root;
    }

    /**
     * Build a shapeless crafting recipe JSON (minecraft:crafting_shapeless).
     */
    public JsonObject shapeless(RecipeEntry output, RecipeEntry.MultiInput inputs)
    {
        JsonObject root = new JsonObject();
        root.addProperty("id", output.getId().getPath() + "_shapeless_recipe." + UUID.randomUUID());
        root.addProperty("type", SupportedRecipeType.CRAFTING_TABLE_SHAPELESS.getId());

        JsonArray ingredients = new JsonArray();
        for(RecipeEntry entry : inputs.getEntries())
        {
            ingredients.add(singletonItemJson(entry));
        }
        root.add("ingredients", ingredients);

        JsonObject result = new JsonObject();
        result.addProperty("item", output.getId().toString());
        result.addProperty("count", output.getCount());
        root.add("result", result);

        return root;
    }
}