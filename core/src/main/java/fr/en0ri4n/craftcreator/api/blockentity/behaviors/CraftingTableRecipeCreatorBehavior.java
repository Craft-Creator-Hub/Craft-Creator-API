package fr.en0ri4n.craftcreator.api.blockentity.behaviors;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityBehavior;
import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityContext;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;

public class CraftingTableRecipeCreatorBehavior implements BlockEntityBehavior {

    private static final String KEY_CRAFTING_TYPE = "crafting_table_recipe_creator.type";

    // Example constants
    private boolean isCraftingShapeless = true;

    @Override
    public void tick(CoreBlockEntity entity, BlockEntityContext ctx) {}

    @Override
    public void save(CoreBlockEntity entity, JsonObject out) {
        out.addProperty(KEY_CRAFTING_TYPE, isCraftingShapeless ? "shapeless" : "shaped");
    }

    @Override
    public void load(CoreBlockEntity entity, JsonObject in) {
        if (in.has(KEY_CRAFTING_TYPE)) {
            String type = in.get(KEY_CRAFTING_TYPE).getAsString();
            isCraftingShapeless = type.equals("shapeless");
        }
    }
}