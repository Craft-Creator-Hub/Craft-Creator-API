package fr.en0ri4n.craftcreator.recipes.managers;

import fr.en0ri4n.craftcreator.base.RecipeCreator;
import fr.en0ri4n.craftcreator.container.slot.utils.PositionnedSlot;
import fr.en0ri4n.craftcreator.recipes.base.BaseRecipesManager;
import fr.en0ri4n.craftcreator.recipes.base.ModRecipeSerializer;
import fr.en0ri4n.craftcreator.recipes.serializers.MinecraftRecipeSerializer;
import fr.en0ri4n.craftcreator.utils.SlotHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fr.en0ri4n.craftcreator.base.ModRecipeCreators.*;

public class MinecraftRecipeManager extends BaseRecipesManager
{
    private static final MinecraftRecipeManager INSTANCE = new MinecraftRecipeManager();

    @Override
    public void createRecipe(RecipeCreator recipe, List<Slot> slots, RecipeInfos recipeInfos, ModRecipeSerializer.SerializerType serializerType)
    {
        MinecraftRecipeSerializer.get().setSerializerType(serializerType);

        List<SlotItemHandler> currentSlots = PositionnedSlot.getSlotsFor(recipe.getSlots(), slots);
        Map<Integer, ResourceLocation> taggedSlots = recipeInfos.getMap(RecipeInfos.Parameters.TAGGED_SLOTS);

        if(recipe.is(CRAFTING_TABLE))
            createCraftingTableRecipe(currentSlots, taggedSlots, recipeInfos.getList(RecipeInfos.Parameters.NBT_SLOTS), recipeInfos.getBoolean(RecipeInfos.Parameters.SHAPED));
        else if(recipe.is(FURNACE_SMELTING, FURNACE_BLASTING, FURNACE_SMOKING, CAMPFIRE_COOKING))
            createFurnaceRecipe(recipe, currentSlots, taggedSlots, recipeInfos.getValue(RecipeInfos.Parameters.EXPERIENCE).doubleValue(), recipeInfos.getValue(RecipeInfos.Parameters.COOKING_TIME).intValue());
        else if(recipe.is(SMITHING))
            createSmithingTableRecipe(currentSlots);
        else if(recipe.is(STONECUTTING))
            createStoneCutterRecipe(currentSlots);
    }

    private void createFurnaceRecipe(RecipeCreator recipe, List<SlotItemHandler> slots, Map<Integer, ResourceLocation> taggedSlots, double experience, int cookingTime)
    {
        if(areSlotsEmpty(slots, SlotHelper.FURNACE_SLOTS_INPUT.size(), SlotHelper.FURNACE_SLOTS_OUTPUT.size()))
            return;

        RecipeEntry.Input input = getSingleInput(taggedSlots, slots.get(0));
        RecipeEntry.Output output = getSingleOutput(slots.get(1));

        MinecraftRecipeSerializer.get().serializeFurnaceRecipe(recipe, input, output, experience, cookingTime);
    }

    public void createSmithingTableRecipe(List<SlotItemHandler> slots)
    {
        if(areSlotsEmpty(slots, 2, 1))
            return;

        RecipeEntry.Input base = getSingleInput(Collections.emptyMap(), slots.get(0));
        RecipeEntry.Input addition = getSingleInput(Collections.emptyMap(), slots.get(1));

        RecipeEntry.Output output = getSingleOutput(slots.get(2));

        MinecraftRecipeSerializer.get().serializeSmithingRecipe(base, addition, output);
    }

    public void createStoneCutterRecipe(List<SlotItemHandler> slots)
    {
        if(areSlotsEmpty(slots, 1, 1))
            return;

        RecipeEntry.Input input = getSingleInput(Collections.emptyMap(), slots.get(0));
        RecipeEntry.Output output = getSingleOutput(slots.get(1));

        MinecraftRecipeSerializer.get().serializeStoneCutterRecipe(input, output);
    }

    public void createCraftingTableRecipe(List<SlotItemHandler> slots, Map<Integer, ResourceLocation> taggedSlots, List<Integer> nbtSlots, boolean shaped)
    {
        if(areSlotsEmpty(slots, 9, 1))
            return;

        ItemStack output = slots.get(9).getItem();

        MinecraftRecipeSerializer.get().serializeCraftingTableRecipe(output, slots, taggedSlots, nbtSlots, shaped);
    }

    public static MinecraftRecipeManager get()
    {
        return INSTANCE;
    }
}
