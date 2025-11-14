package fr.en0ri4n.craftcreator.recipes.serializers;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.recipe.model.CraftingGrid;
import fr.en0ri4n.craftcreator.api.recipe.serialize.CraftingTableRecipeSerializer;
import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.base.RecipeCreator;
import fr.en0ri4n.craftcreator.base.SupportedMods;
import fr.en0ri4n.craftcreator.recipes.base.ModRecipeSerializer;
import fr.en0ri4n.craftcreator.recipes.utils.CraftIngredients;
import fr.en0ri4n.craftcreator.utils.Identifier;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.items.SlotItemHandler;

import java.util.List;
import java.util.Map;

public class MinecraftRecipeSerializer extends ModRecipeSerializer
{
    private static final MinecraftRecipeSerializer INSTANCE = new MinecraftRecipeSerializer();

    private MinecraftRecipeSerializer()
    {
        super(SupportedMods.MINECRAFT);
    }

    public void serializeFurnaceRecipe(RecipeCreator smeltType, RecipeEntry.Input input, RecipeEntry.Output output, double experience, int cookTime)
    {
        JsonObject obj = createBaseJson(smeltType.getRecipeType());
        obj.add("ingredient", singletonItemJsonObject(input));
        obj.addProperty("experience", experience);
        obj.addProperty("cookingtime", cookTime);
        obj.addProperty("result", output.getRegistryName().toString());

        addRecipeTo(obj, smeltType.getRecipeType(), output.getRegistryName());
    }

    public void serializeStoneCutterRecipe(RecipeEntry.Input input, RecipeEntry.Output output)
    {
        JsonObject obj = createBaseJson(RecipeType.STONECUTTING);
        obj.add("ingredient", singletonItemJsonObject(input));
        obj.addProperty("result", output.getRegistryName().toString());
        obj.addProperty("count", output.count());

        addRecipeTo(obj, RecipeType.STONECUTTING, output.getRegistryName());
    }

    public void serializeSmithingRecipe(RecipeEntry.Input base, RecipeEntry.Input addition, RecipeEntry.Output output)
    {
        JsonObject obj = createBaseJson(RecipeType.SMITHING);
        obj.add("base", singletonItemJsonObject(base));
        obj.add("addition", singletonItemJsonObject(addition));
        obj.add("result", singletonItemJsonObject(output));

        addRecipeTo(obj, RecipeType.SMITHING, output.getRegistryName());
    }

    public void serializeCraftingTableRecipe(ItemStack output,
                                             List<SlotItemHandler> slots,
                                             Map<Integer, ResourceLocation> taggedSlots,
                                             List<Integer> nbtSlots,
                                             boolean shaped) {
        Identifier resultId = Identifier.from(output.getItem().getRegistryName().toString());

        JsonObject obj;
        if (shaped) {
            CraftingGrid grid = new CraftingGrid(3, 3);
            for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
                SlotItemHandler slot = slots.get(slotIndex);
                ItemStack stack = slot.getItem();
                if (stack.isEmpty()) continue;

                int x = slotIndex % 3;
                int y = slotIndex / 3;

                ResourceLocation rl = stack.getItem().getRegistryName();
                Identifier id = Identifier.from(rl.getNamespace(), rl.getPath());

                boolean isTag = taggedSlots.containsKey(slotIndex);
                Identifier tagId = isTag
                        ? Identifier.from(taggedSlots.get(slotIndex).getNamespace(), taggedSlots.get(slotIndex).getPath())
                        : id;

                RecipeEntry entry = isTag
                        ? RecipeEntry.itemTag(tagId, stack.getCount())
                        : RecipeEntry.item(id, stack.getCount());

                grid.set(x, y, entry);
            }

            obj = CraftingTableRecipeSerializer.shaped(resultId, output.getCount(), grid);
        } else {
            RecipeEntry.MultiInput inputs = new RecipeEntry.MultiInput();
            for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
                SlotItemHandler slot = slots.get(slotIndex);
                ItemStack stack = slot.getItem();
                if (stack.isEmpty()) continue;

                ResourceLocation rl = stack.getItem().getRegistryName();
                Identifier id = Identifier.from(rl.getNamespace(), rl.getPath());

                boolean isTag = taggedSlots.containsKey(slotIndex);
                Identifier tagId = isTag
                        ? Identifier.from(taggedSlots.get(slotIndex).getNamespace(), taggedSlots.get(slotIndex).getPath())
                        : id;

                RecipeEntry entry = isTag
                        ? RecipeEntry.itemTag(tagId, stack.getCount())
                        : RecipeEntry.item(id, stack.getCount());

                inputs.add(entry);
            }

            obj = CraftingTableRecipeSerializer.shapeless(resultId, output.getCount(), inputs);
        }

        // NBT handling is loader-specific, so keep it here:
        if (nbtSlots.contains(9)) {
            JsonObject resultObj = obj.getAsJsonObject("result");
            resultObj.addProperty("type", "forge:nbt");
            CompoundTag nbt = slots.get(9).getItem().getTag();
            if (nbt != null) {
                nbt.remove("display");
                resultObj.addProperty("nbt", escape(nbt.toString(), false));
            }
        }

        addRecipeTo(obj, RecipeType.CRAFTING, output.getItem().getRegistryName());
    }


    @Override
    public CraftIngredients getInput(Recipe<?> recipe)
    {
        CraftIngredients inputIngredients = CraftIngredients.create();

        if(recipe instanceof UpgradeRecipe) // Fields are not accessible so we need to do this :(
        {
            UpgradeRecipe smithingRecipe = (UpgradeRecipe) recipe;
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            UpgradeRecipe.Serializer serializer = (UpgradeRecipe.Serializer) smithingRecipe.getSerializer();
            serializer.toNetwork(buffer, smithingRecipe);
            Ingredient base = Ingredient.fromNetwork(buffer);
            Ingredient addition = Ingredient.fromNetwork(buffer);
            inputIngredients.addIngredient(new CraftIngredients.ItemIngredient(base.getItems()[0].getItem().getRegistryName(), 1, "Base"));
            inputIngredients.addIngredient(new CraftIngredients.ItemIngredient(addition.getItems()[0].getItem().getRegistryName(), 1, "Addition"));
        }
        else if(recipe instanceof AbstractCookingRecipe)
        {
            AbstractCookingRecipe abstractCookingRecipe = (AbstractCookingRecipe) recipe;
            putIfNotEmpty(inputIngredients, abstractCookingRecipe.getIngredients());
            inputIngredients.addIngredient(new CraftIngredients.DataIngredient("Cooking Time", CraftIngredients.DataIngredient.DataUnit.TICK, abstractCookingRecipe.getCookingTime(), false));
            inputIngredients.addIngredient(new CraftIngredients.DataIngredient("Experience", CraftIngredients.DataIngredient.DataUnit.EXPERIENCE, abstractCookingRecipe.getExperience(), false));
        }

        if(inputIngredients.isEmpty()) putIfNotEmpty(inputIngredients, recipe.getIngredients());

        return inputIngredients;
    }

    @Override
    public CraftIngredients getOutput(Recipe<?> recipe)
    {
        CraftIngredients ingredients = CraftIngredients.create();

        if(recipe instanceof CraftingRecipe)
        {
            CraftingRecipe craftingRecipe = (CraftingRecipe) recipe;
            ingredients.addIngredient(new CraftIngredients.ItemIngredient(recipe.getResultItem().getItem().getRegistryName(), recipe.getResultItem().getCount()));
            if(craftingRecipe.getResultItem().hasTag())
                ingredients.addIngredient(new CraftIngredients.NBTIngredient(craftingRecipe.getResultItem().getTag()));
        }

        if(ingredients.isEmpty())
            ingredients.addIngredient(new CraftIngredients.ItemIngredient(recipe.getResultItem().getItem().getRegistryName(), recipe.getResultItem().getCount()));

        return ingredients;
    }

    public static MinecraftRecipeSerializer get()
    {
        return INSTANCE;
    }
}
