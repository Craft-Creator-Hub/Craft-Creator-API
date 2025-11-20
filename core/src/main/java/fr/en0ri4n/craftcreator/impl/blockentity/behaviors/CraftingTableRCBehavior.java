package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CraftingTableRCBehavior extends RecipeCreatorBlockEntityBehavior
{

    public static final String CRAFTING_TYPE_KEY = "crafting_table_recipe_creator.type";

    private boolean isCraftingShapeless = true;

    @Override
    public void save(CoreBlockEntity entity, JsonObject out)
    {
        super.save(entity, out);
        out.addProperty(CRAFTING_TYPE_KEY, isCraftingShapeless ? "shapeless" : "shaped");
    }

    @Override
    public void load(CoreBlockEntity entity, JsonObject in)
    {
        super.load(entity, in);
        if(in.has(CRAFTING_TYPE_KEY))
        {
            String type = in.get(CRAFTING_TYPE_KEY).getAsString();
            isCraftingShapeless = type.equals("shapeless");
        }
    }
}