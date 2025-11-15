package fr.en0ri4n.craftcreator.api.serializer;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.mod.SupportedMods;
import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.HashSet;
import java.util.Set;

public abstract class VanillaCoreRecipeSerializer extends CoreModRecipeSerializer {

    protected VanillaCoreRecipeSerializer() {
        super(SupportedMods.MINECRAFT, RecipeOutputTarget.DATAPACK);
    }

    public void serializeFurnaceRecipe(Identifier furnaceTypeId,
                                       RecipeEntry input,
                                       RecipeEntry output,
                                       double experience,
                                       int cookTime) {
        JsonObject obj = createBaseJson(furnaceTypeId);
        obj.add("ingredient", singletonItemJsonObject("name", input.getId().toString()));
        obj.addProperty("experience", experience);
        obj.addProperty("cookingtime", cookTime);
        obj.addProperty("result", output.getId().toString());

        addRecipe(obj, furnaceTypeId, output.getId(), null);
    }

    public void serializeStonecutterRecipe(Identifier stonecutTypeId,
                                           RecipeEntry input,
                                           RecipeEntry output) {
        JsonObject obj = createBaseJson(stonecutTypeId);
        obj.add("ingredient", singletonItemJsonObject("name", input.getId().toString()));
        obj.addProperty("result", output.getId().toString());
        obj.addProperty("count", output.getCount());

        addRecipe(obj, stonecutTypeId, output.getId(), null);
    }

    // And you already moved crafting table logic to CraftingTableRecipeSerializer in core
}