package fr.en0ri4n.craftcreator.platform;

import com.google.gson.JsonElement;
import fr.en0ri4n.craftcreator.api.platform.GameDataProvider;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ForgeGameDataProvider implements GameDataProvider
{
    private static final int GRID_SIZE = 3;

    @Override
    public List<Identifier> getItemIds()
    {
        return ForgeRegistries.ITEMS.getKeys().stream().map(ForgeGameDataProvider::toCore).sorted(Comparator.comparing(Identifier::toString)).toList();
    }

    @Override
    public List<String> getModIds()
    {
        return ModList.get().getMods().stream().map(IModInfo::getModId).sorted().toList();
    }

    @Override
    public List<Identifier> getRecipeTypeIds()
    {
        return ForgeRegistries.RECIPE_SERIALIZERS.getKeys().stream().map(ForgeGameDataProvider::toCore).sorted(Comparator.comparing(Identifier::toString)).toList();
    }

    @Override
    public List<Recipe> getClientRecipes()
    {
        // Kept in a nested class so dedicated servers never load client classes
        return ClientRecipes.get();
    }

    private static Identifier toCore(ResourceLocation rl)
    {
        return Identifier.from(rl.getNamespace(), rl.getPath());
    }

    private static class ClientRecipes
    {
        private static List<Recipe> get()
        {
            if(Minecraft.getInstance().level == null) return List.of();

            List<Recipe> recipes = new ArrayList<>();
            for(net.minecraft.world.item.crafting.Recipe<?> recipe : Minecraft.getInstance().level.getRecipeManager().getRecipes())
            {
                ResourceLocation serializerId = recipe.getSerializer().getRegistryName();
                if(serializerId == null) serializerId = Registry.RECIPE_TYPE.getKey(recipe.getType());
                if(serializerId == null) continue;

                List<RecipeEntry> inputs = new ArrayList<>();
                List<Ingredient> ingredients = recipe.getIngredients();
                int width = recipe instanceof IShapedRecipe<?> shaped ? shaped.getRecipeWidth() : 0;
                for(int i = 0; i < ingredients.size(); i++)
                {
                    RecipeEntry input = toEntry(ingredients.get(i));
                    if(input == null) continue;

                    input.setSlot(width > 0 ? (i / width) * GRID_SIZE + i % width : i);
                    inputs.add(input);
                }

                List<RecipeEntry> outputs = new ArrayList<>();
                ItemStack result = recipe.getResultItem();
                if(!result.isEmpty())
                {
                    RecipeEntry output = RecipeEntry.item(toCore(result.getItem().getRegistryName()), result.getCount());
                    output.setType(RecipeEntry.EntryType.OUTPUT);
                    outputs.add(output);
                }

                RecipeInfos infos = RecipeInfos.create();
                if(recipe instanceof AbstractCookingRecipe cooking)
                {
                    infos.setInt("cookingtime", cooking.getCookingTime());
                    infos.setDouble("experience", cooking.getExperience());
                }

                Identifier id = toCore(recipe.getId());
                recipes.add(new Recipe(id, id.toString(), toCore(serializerId), inputs, outputs, infos));
            }

            recipes.sort(Comparator.comparing(recipe -> recipe.getId().toString()));
            return recipes;
        }

        /**
         * A single tag becomes a tag entry, anything else an item entry listing every accepted item.
         */
        private static RecipeEntry toEntry(Ingredient ingredient)
        {
            ItemStack[] stacks = ingredient.getItems();
            if(stacks.length == 0 || stacks[0].isEmpty()) return null;

            String tag = getSingleTag(ingredient);
            if(tag != null)
                return RecipeEntry.itemTag(Identifier.from(tag), stacks[0].getCount());

            RecipeEntry entry = RecipeEntry.item(toCore(stacks[0].getItem().getRegistryName()), stacks[0].getCount());
            if(stacks.length > 1)
                entry.setAlternatives(Arrays.stream(stacks).map(stack -> toCore(stack.getItem().getRegistryName())).distinct().toList());
            return entry;
        }

        private static String getSingleTag(Ingredient ingredient)
        {
            try
            {
                JsonElement json = ingredient.toJson();
                return json.isJsonObject() && json.getAsJsonObject().has("tag") ? json.getAsJsonObject().get("tag").getAsString() : null;
            }
            catch(Exception e)
            {
                return null; // some modded ingredients can't be written to json
            }
        }
    }
}
