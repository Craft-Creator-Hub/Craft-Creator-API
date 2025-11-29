package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CraftingTableRCBehavior extends RecipeCreatorBlockEntityBehavior
{
    public static final String CRAFTING_TYPE_KEY = "crafting_type";

    private CraftingType craftingType = CraftingType.SHAPED;

    @Override
    public void save(CoreBlockEntity entity, JsonObject out)
    {
        super.save(entity, out);
        out.addProperty(CRAFTING_TYPE_KEY, craftingType.name().toLowerCase());
    }

    @Override
    public void load(CoreBlockEntity entity, JsonObject in)
    {
        super.load(entity, in);
        if(in.has(CRAFTING_TYPE_KEY))
        {
            String type = in.get(CRAFTING_TYPE_KEY).getAsString();
            this.craftingType = CraftingType.valueOf(type.toUpperCase());
        }
    }

    @Getter
    @AllArgsConstructor
    public enum CraftingType {
        SHAPED(Identifier.from("minecraft:crafting_shaped")),
        SHAPELESS(Identifier.from("minecraft:crafting_shapeless"));

        private final Identifier recipeTypeId;
    }
}