package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
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
import java.util.function.Function;

@NoArgsConstructor
public class CraftingTableRecipeSerializer extends RecipeSerializer
{
    public static final CraftingTableRecipeSerializer INSTANCE = new CraftingTableRecipeSerializer();
    public static RecipeSerializer get() { return INSTANCE; }

    private static final int GRID_SIZE = 3;
    private static final int OUTPUT_SLOT = GRID_SIZE * GRID_SIZE;

    @Override
    protected void processInventory(List<RecipeEntry> entries, List<CoreItemStack> inventory, Map<Integer, Identifier> taggedSlots)
    {
        for(int i = 0; i < OUTPUT_SLOT; i++)
        {
            CoreItemStack stack = inventory.get(i);
            RecipeEntry entry;

            if(taggedSlots.containsKey(i)) entry = RecipeEntry.itemTag(taggedSlots.get(i), stack.getCount());
            else if(!stack.isEmpty()) entry = RecipeEntry.item(stack.getItemId(), stack.getCount());
            else continue;

            entry.setSlot(i);
            entries.add(entry);
        }

        CoreItemStack resultStack = inventory.get(OUTPUT_SLOT);
        if(!resultStack.isEmpty())
        {
            RecipeEntry resultEntry = RecipeEntry.item(resultStack.getItemId(), resultStack.getCount());
            resultEntry.setType(RecipeEntry.EntryType.OUTPUT);
            resultEntry.setSlot(OUTPUT_SLOT);
            entries.add(resultEntry);
        }
    }

    @Override
    public JsonObject serialize(CoreBlockEntity coreBlockEntity)
    {
        CraftingTableRCBehavior behavior = (CraftingTableRCBehavior) coreBlockEntity.getBehavior();

        List<RecipeEntry> entries = processBlockEntity(coreBlockEntity);
        List<RecipeEntry> inputs = entries.stream().filter(entry -> entry.getType() == RecipeEntry.EntryType.INPUT).toList();
        RecipeEntry outputEntry = findFirst(entries, RecipeEntry.EntryType.OUTPUT);

        return switch(behavior.getCraftingType())
        {
            case SHAPED ->
            {
                CraftingGrid grid = new CraftingGrid(GRID_SIZE, GRID_SIZE);
                inputs.forEach(entry -> grid.set(entry.getSlot() % GRID_SIZE, entry.getSlot() / GRID_SIZE, entry));
                yield shaped(outputEntry, grid);
            }
            case SHAPELESS -> shapeless(outputEntry, inputs);
        };
    }

    @Override
    public Recipe deserializeToRecipe(JsonObject element)
    {
        if(element == null || !element.has("type")) return Recipe.EMPTY;

        String type = element.get("type").getAsString();
        if(type.equals(CraftingTableRCBehavior.CraftingType.SHAPED.getRecipeTypeId().toString()))
            return deserialize(element, "shaped_recipe", this::parseShapedInputs);
        if(type.equals(CraftingTableRCBehavior.CraftingType.SHAPELESS.getRecipeTypeId().toString()))
            return deserialize(element, "shapeless_recipe", this::parseShapelessInputs);

        return Recipe.EMPTY;
    }

    private Recipe deserialize(JsonObject element, String defaultName, Function<JsonObject, List<RecipeEntry>> inputsParser)
    {
        try
        {
            Identifier id = Identifier.fromMod(element.has("id") ? element.get("id").getAsString() : "unknown");
            String name = element.has("name") ? element.get("name").getAsString() : defaultName;
            Identifier type = Identifier.from(element.get("type").getAsString());

            List<RecipeEntry> outputs = new ArrayList<>();
            RecipeEntry out = element.has("result") && element.get("result").isJsonObject() ? parseResultObject(element.getAsJsonObject("result")) : null;
            if(out != null)
            {
                out.setType(RecipeEntry.EntryType.OUTPUT);
                out.setSlot(OUTPUT_SLOT);
                outputs.add(out);
            }

            return new Recipe(id, name, type, inputsParser.apply(element), outputs, RecipeInfos.create());
        }
        catch(Exception e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to deserialize crafting recipe: " + element, e);
            return Recipe.EMPTY;
        }
    }

    private List<RecipeEntry> parseShapelessInputs(JsonObject element)
    {
        List<RecipeEntry> inputs = new ArrayList<>();
        if(!element.has("ingredients") || !element.get("ingredients").isJsonArray()) return inputs;

        JsonArray ingredients = element.getAsJsonArray("ingredients");
        for(int i = 0; i < ingredients.size(); i++)
            addInput(inputs, parseIngredient(ingredients.get(i)), i);

        return inputs;
    }

    private List<RecipeEntry> parseShapedInputs(JsonObject element)
    {
        List<RecipeEntry> inputs = new ArrayList<>();
        if(!element.has("pattern")) return inputs;

        JsonArray pattern = element.getAsJsonArray("pattern");
        JsonObject key = element.has("key") ? element.getAsJsonObject("key") : new JsonObject();

        for(int row = 0; row < Math.min(pattern.size(), GRID_SIZE); row++)
        {
            String rowStr = pattern.get(row).getAsString();
            for(int col = 0; col < Math.min(rowStr.length(), GRID_SIZE); col++)
            {
                String symbol = String.valueOf(rowStr.charAt(col));
                JsonElement mapping = key.get(symbol);
                if(mapping != null) addInput(inputs, parseIngredient(mapping), row * GRID_SIZE + col);
            }
        }

        return inputs;
    }

    private static void addInput(List<RecipeEntry> inputs, RecipeEntry entry, int slot)
    {
        if(entry == null) return;
        entry.setType(RecipeEntry.EntryType.INPUT);
        entry.setSlot(slot);
        inputs.add(entry);
    }

    /**
     * Build a shaped crafting recipe JSON (minecraft:crafting_shaped).
     */
    public JsonObject shaped(RecipeEntry result, CraftingGrid grid)
    {
        JsonObject root = new JsonObject();
        root.addProperty("type", CraftingTableRCBehavior.CraftingType.SHAPED.getRecipeTypeId().toString());

        PatternAndKey pk = buildPatternAndKey(grid);
        root.add("pattern", pk.pattern());
        root.add("key", pk.key());
        root.add("result", resultJson(result));

        return withIdAndName(root, result.getId().getPath() + "_shaped");
    }

    /**
     * Build a shapeless crafting recipe JSON (minecraft:crafting_shapeless).
     */
    public JsonObject shapeless(RecipeEntry output, List<RecipeEntry> inputs)
    {
        JsonObject root = new JsonObject();
        root.addProperty("type", CraftingTableRCBehavior.CraftingType.SHAPELESS.getRecipeTypeId().toString());

        JsonArray ingredients = new JsonArray();
        inputs.forEach(entry -> ingredients.add(ingredientJson(entry)));
        root.add("ingredients", ingredients);
        root.add("result", resultJson(output));

        return withIdAndName(root, output.getId().getPath() + "_shapeless");
    }
}
