package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.JsonObject;
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
        Identifier recipeId = in.has("id") ? Identifier.from(in.get("id").getAsString()) : Identifier.from("craftcreator:unknown_furnace_recipe");
        Identifier typeId = Identifier.from(in.get("type").getAsString());
        RecipeEntry inputEntry = parseIngredientObject(in.getAsJsonObject("ingredient"));
        RecipeEntry outputEntry = RecipeEntry.item(Identifier.from(in.get("result").getAsString()), 1);
        outputEntry.setType(RecipeEntry.EntryType.OUTPUT);
        float experience = in.get("experience").getAsFloat();
        int cookingTime = in.get("cookingtime").getAsInt();
        RecipeInfos infos = RecipeInfos.create();
        infos.addParameter(new RecipeInfos.RecipeParameterNumber("experience", experience, true));
        infos.addParameter(new RecipeInfos.RecipeParameterNumber("cookingtime", cookingTime, true));
        return new Recipe(recipeId, typeId, List.of(inputEntry), List.of(outputEntry), infos);
    }

    @Override
    public JsonObject serialize(CoreBlockEntity value)
    {
        JsonObject out = new JsonObject();

        FurnaceRCBehavior behavior = (FurnaceRCBehavior) value.getBehavior();
        List<RecipeEntry> entries = processBlockEntity(value);

        RecipeEntry inputEntry = entries.stream()
                .filter(entry -> entry.getType() == RecipeEntry.EntryType.INPUT)
                .findFirst()
                .orElse(RecipeEntry.EMPTY);

        RecipeEntry outputEntry = entries.stream()
                .filter(entry -> entry.getType() == RecipeEntry.EntryType.OUTPUT)
                .findFirst()
                .orElse(RecipeEntry.EMPTY);

        out.addProperty("type", behavior.getFurnaceType().getRecipeTypeId().toString());
        out.add("ingredient", singletonItemJson(inputEntry));

        out.addProperty("result", outputEntry.getId().toString());
        out.addProperty("experience", behavior.getExperience());
        out.addProperty("cookingtime", behavior.getCookingTime());

        return out;
    }
}
