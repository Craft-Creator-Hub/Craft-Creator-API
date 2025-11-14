package fr.en0ri4n.craftcreator.api.recipe.minecraft;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.recipe.AbstractCCRecipe;
import fr.en0ri4n.craftcreator.utils.Identifier;

public class ShapelessCraftingTableCCRecipe extends AbstractCCRecipe
{
    public ShapelessCraftingTableCCRecipe()
    {

    }

    @Override
    public void deserialize(JsonObject jsonObject)
    {

    }

    @Override
    public JsonObject serialize()
    {
        return null;
    }

    @Override
    public Identifier getRecipeType()
    {
        return Identifier.from("minecraft:crafting_shapeless");
    }

    @Override
    public String toString()
    {
        return "";
    }
}
