package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.FurnaceRCBehavior;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;
import java.util.Map;

public class FurnaceRecipeSerializer extends RecipeSerializer
{
    private static final FurnaceRecipeSerializer INSTANCE = new FurnaceRecipeSerializer();
    public static FurnaceRecipeSerializer get() { return INSTANCE; }

    @Override
    protected void processInventory(List<RecipeEntry> entries, List<CoreItemStack> inventory, Map<Integer, Identifier> taggedSlots)
    {
        for(int i = 0; i < inventory.size() - 1; i++)
        {
            CoreItemStack inputStack = inventory.get(i);
            if(!inputStack.isEmpty())
            {
                RecipeEntry inputEntry = taggedSlots.containsKey(i) ? RecipeEntry.itemTag(taggedSlots.get(i), inputStack.getCount()) : RecipeEntry.item(inputStack.getItemId(), inputStack.getCount());
                entries.add(inputEntry);
            }
        }

        CoreItemStack outputStack = inventory.get(inventory.size() - 1);
        if(!outputStack.isEmpty())
        {
            RecipeEntry outputEntry = RecipeEntry.item(outputStack.getItemId(), outputStack.getCount());
            outputEntry.setType(RecipeEntry.EntryType.OUTPUT);
            entries.add(outputEntry);
        }
    }

    @Override
    public Recipe deserializeToRecipe(JsonObject in)
    {
        try
        {
            Identifier recipeId = Identifier.fromMod(in.has("id") ? in.get("id").getAsString() : "unknown_furnace_recipe");
            String name = in.has("name") ? in.get("name").getAsString() : "furnace_recipe";
            Identifier typeId = Identifier.from(in.get("type").getAsString());

            RecipeEntry inputEntry = parseIngredient(in.get("ingredient"));
            if(inputEntry == null) return Recipe.EMPTY;
            inputEntry.setSlot(0);

            RecipeEntry outputEntry = in.get("result").isJsonObject()
                    ? parseResultObject(in.getAsJsonObject("result"))
                    : RecipeEntry.item(Identifier.from(in.get("result").getAsString()), 1);
            if(outputEntry == null) return Recipe.EMPTY;
            outputEntry.setType(RecipeEntry.EntryType.OUTPUT);
            outputEntry.setSlot(1);

            RecipeInfos infos = RecipeInfos.create();
            infos.addParameter(new RecipeInfos.RecipeParameterNumber("experience", in.get("experience").getAsFloat(), true));
            infos.addParameter(new RecipeInfos.RecipeParameterNumber("cookingtime", in.get("cookingtime").getAsInt(), true));

            return new Recipe(recipeId, name, typeId, List.of(inputEntry), List.of(outputEntry), infos);
        }
        catch(Exception e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to deserialize furnace recipe: " + in, e);
            return Recipe.EMPTY;
        }
    }

    @Override
    public JsonObject serialize(CoreBlockEntity value)
    {
        FurnaceRCBehavior behavior = (FurnaceRCBehavior) value.getBehavior();
        List<RecipeEntry> entries = processBlockEntity(value);

        RecipeEntry inputEntry = findFirst(entries, RecipeEntry.EntryType.INPUT);
        RecipeEntry outputEntry = findFirst(entries, RecipeEntry.EntryType.OUTPUT);
        Identifier typeId = behavior.getFurnaceType().getRecipeTypeId();

        JsonObject out = new JsonObject();
        out.addProperty("type", typeId.toString());
        out.add("ingredient", ingredientJson(inputEntry));
        out.addProperty("result", outputEntry.getId().toString());
        out.addProperty("experience", behavior.getExperience());
        out.addProperty("cookingtime", behavior.getCookingTime());

        return withIdAndName(out, outputEntry.getId().getPath() + "_from_" + typeId.getPath());
    }
}
