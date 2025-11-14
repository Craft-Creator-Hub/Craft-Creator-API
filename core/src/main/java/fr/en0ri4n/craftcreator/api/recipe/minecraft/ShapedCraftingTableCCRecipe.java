package fr.en0ri4n.craftcreator.api.recipe.minecraft;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.recipe.AbstractCCRecipe;
import fr.en0ri4n.craftcreator.utils.Identifier;

public class ShapedCraftingTableCCRecipe extends AbstractCCRecipe
{
    public ShapedCraftingTableCCRecipe()
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
        return Identifier.from("minecraft:crafting_shaped");
    }

    @Override
    public String toString()
    {
        return "";
    }
}
