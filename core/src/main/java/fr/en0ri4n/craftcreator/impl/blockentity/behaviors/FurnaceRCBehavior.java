package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FurnaceRCBehavior extends RecipeCreatorBlockEntityBehavior
{
    private static final String FURNACE_TYPE_KEY = "furnace_type";
    private static final String EXPERIENCE_KEY = "experience";
    private static final String COOKING_TIME_KEY = "cooking";
    private FurnaceType furnaceType = FurnaceType.FURNACE;
    private float experience = 0.0f;
    private int cookingTime = 200;

    @Override
    public void save(CoreBlockEntity entity, JsonObject out)
    {
        super.save(entity, out);
        out.addProperty(FURNACE_TYPE_KEY, furnaceType.name().toLowerCase());
        out.addProperty(EXPERIENCE_KEY, experience);
        out.addProperty(COOKING_TIME_KEY, cookingTime);
    }

    @Override
    public void load(CoreBlockEntity entity, JsonObject in)
    {
        super.load(entity, in);
        if(in.has(FURNACE_TYPE_KEY))
            this.furnaceType = FurnaceType.valueOf(in.get(FURNACE_TYPE_KEY).getAsString().toUpperCase());
        if(in.has(EXPERIENCE_KEY))
            this.experience = in.get(EXPERIENCE_KEY).getAsFloat();
        if(in.has(COOKING_TIME_KEY))
            this.cookingTime = in.get(COOKING_TIME_KEY).getAsInt();
    }

    @Getter
    @AllArgsConstructor
    public enum FurnaceType {
        FURNACE(Identifier.from("minecraft:smelting"), Identifier.from("minecraft:furnace")),
        BLAST_FURNACE(Identifier.from("minecraft:blasting"), Identifier.from("minecraft:blast_furnace")),
        SMOKER(Identifier.from("minecraft:smoking"), Identifier.from("minecraft:smoker"));

        private final Identifier recipeTypeId;
        private final Identifier itemId;
    }
}
