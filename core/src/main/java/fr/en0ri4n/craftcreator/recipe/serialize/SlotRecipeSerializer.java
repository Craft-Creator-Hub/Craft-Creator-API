package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.SlotRecipeBehavior;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.slot.SlotRecipeContext;
import fr.en0ri4n.craftcreator.recipe.slot.SlotRecipeType;
import fr.en0ri4n.craftcreator.recipe.slot.SlotSpec;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serializer for every {@link SlotRecipeType}: the selected type's writer produces the json.
 */
public class SlotRecipeSerializer extends RecipeSerializer
{
    /** Json keys holding inputs, in the order their entries fill the input slots when reading a recipe back. */
    private static final List<String> INPUT_KEYS = List.of("ingredient", "ingredients", "input", "base", "addition", "catalyst", "reagent", "trunk", "leaves");
    private static final List<String> OUTPUT_KEYS = List.of("result", "results", "output");

    private final SlotRecipeType type;

    public SlotRecipeSerializer(SlotRecipeType type)
    {
        this.type = type;
    }

    private SlotRecipeContext createContext(CoreBlockEntity cbe)
    {
        SlotRecipeBehavior behavior = (SlotRecipeBehavior) cbe.getBehavior();
        return new SlotRecipeContext(type, cbe.getInventory(), behavior.getTaggedSlots(), behavior.getParams());
    }

    /**
     * Some types have no output slot (e.g. Botania brews, whose result is typed in the settings).
     */
    @Override
    public boolean isBlockDataValid(CoreBlockEntity cbe)
    {
        if(type.getSlots().stream().anyMatch(SlotSpec::output)) return super.isBlockDataValid(cbe);
        return cbe.getBehavior() instanceof SlotRecipeBehavior && !processBlockEntity(cbe).isEmpty();
    }

    @Override
    public List<RecipeEntry> processBlockEntity(CoreBlockEntity cbe)
    {
        return createContext(cbe).entries();
    }

    @Override
    protected void processInventory(List<RecipeEntry> entries, List<CoreItemStack> inventory, Map<Integer, Identifier> taggedSlots)
    {
        // Unused: entries depend on the selected type, see processBlockEntity
    }

    @Override
    public JsonObject serialize(CoreBlockEntity cbe)
    {
        SlotRecipeContext ctx = createContext(cbe);

        JsonObject out = new JsonObject();
        out.addProperty("type", type.getRecipeTypeId().toString());
        type.getWriter().write(ctx, out);

        RecipeEntry output = findFirst(ctx.entries(), RecipeEntry.EntryType.OUTPUT);
        String outputName = output == RecipeEntry.EMPTY ? ctx.entries().get(0).getId().getPath() : output.getId().getPath();
        return withIdAndName(out, outputName + "_" + type.getRecipeTypeId().getPath());
    }

    /**
     * Rebuild a displayable recipe from any json made by a slot recipe type:
     * entries found under the usual input/output keys fill the type's input/output slots in order,
     * and top-level numbers become recipe parameters.
     */
    @Override
    public Recipe deserializeToRecipe(JsonObject in)
    {
        try
        {
            Identifier typeId = Identifier.from(in.get("type").getAsString());

            List<RecipeEntry> inputs = new ArrayList<>();
            List<RecipeEntry> outputs = new ArrayList<>();
            INPUT_KEYS.forEach(key -> collect(in.get(key), inputs));
            OUTPUT_KEYS.forEach(key -> collect(in.get(key), outputs));

            assignSlots(type, inputs, false);
            assignSlots(type, outputs, true);

            RecipeInfos infos = RecipeInfos.create();
            in.entrySet().stream()
                    .filter(e -> e.getValue().isJsonPrimitive() && e.getValue().getAsJsonPrimitive().isNumber())
                    .forEach(e -> infos.addParameter(new RecipeInfos.RecipeParameterNumber(e.getKey(), e.getValue().getAsNumber(), true)));

            Identifier id = Identifier.fromMod(in.has("id") ? in.get("id").getAsString() : "unknown");
            String name = in.has("name") ? in.get("name").getAsString() : type.getId().getPath();
            return new Recipe(id, name, typeId, inputs, outputs, infos);
        }
        catch(Exception e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to deserialize recipe: " + in, e);
            return Recipe.EMPTY;
        }
    }

    private void collect(JsonElement element, List<RecipeEntry> into)
    {
        if(element == null || element.isJsonNull()) return;

        if(element.isJsonArray())
        {
            element.getAsJsonArray().forEach(child -> collect(child, into));
            return;
        }

        if(element.isJsonObject())
        {
            JsonObject obj = element.getAsJsonObject();
            String idKey = obj.has("fluid") ? "fluid" : obj.has("block") ? "block" : obj.has("name") ? "name" : null;
            if(idKey != null)
            {
                into.add(RecipeEntry.item(Identifier.from(obj.get(idKey).getAsString()), 1));
                return;
            }

            RecipeEntry entry = obj.has(getIdIdentifier()) && !obj.has("tag") ? parseResultObject(obj) : parseIngredientObject(obj);
            if(entry != null) into.add(entry);
            return;
        }

        RecipeEntry entry = parseIngredient(element);
        if(entry != null) into.add(entry);
    }

    private static void assignSlots(SlotRecipeType type, List<RecipeEntry> entries, boolean output)
    {
        List<SlotSpec> slots = type.getSlots();
        int entryIndex = 0;
        for(int slot = 0; slot < slots.size() && entryIndex < entries.size(); slot++)
        {
            if(slots.get(slot).output() != output) continue;

            RecipeEntry entry = entries.get(entryIndex++);
            entry.setSlot(slot);
            entry.setType(output ? RecipeEntry.EntryType.OUTPUT : RecipeEntry.EntryType.INPUT);
        }
    }
}
