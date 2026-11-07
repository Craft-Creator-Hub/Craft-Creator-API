package fr.en0ri4n.craftcreator.recipe;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.recipe.exporter.RecipeExporterRegistry;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializer;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializerRegistry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.utils.Feedback;
import fr.en0ri4n.craftcreator.serialize.GsonProvider;
import fr.en0ri4n.craftcreator.utils.Identifier;

public class RecipeManager
{
    public static final RecipeManager INSTANCE = new RecipeManager();
    public static RecipeManager get() { return INSTANCE; }

    public RecipeRequestFeedback handleMakeRecipeRequest(CoreBlockEntity coreBlockEntity, Identifier containerId)
    {
        return handleMakeRecipeRequest(coreBlockEntity, containerId, false);
    }

    /**
     * @param preview only make the recipe's json (without "id" and "name", which are Craft Creator's) and send it back,
     *                without exporting the recipe
     */
    public RecipeRequestFeedback handleMakeRecipeRequest(CoreBlockEntity coreBlockEntity, Identifier containerId, boolean preview)
    {
        RecipeSerializer serializer = RecipeSerializerRegistry.get().getByRecipeTypeId(containerId);

        if(serializer == null || !serializer.isBlockDataValid(coreBlockEntity))
            return RecipeRequestFeedback.of(Feedback.INVALID_BLOCK_DATA, false);

        RecipeCreatorBlockEntityBehavior behavior = (RecipeCreatorBlockEntityBehavior) coreBlockEntity.getBehavior();

        JsonObject recipeJson = serializer.serialize(coreBlockEntity);

        if(preview)
        {
            String name = recipeJson.has("name") ? recipeJson.get("name").getAsString() : null;
            JsonObject shown = recipeJson.deepCopy();
            shown.remove("id");
            shown.remove("name");
            return RecipeRequestFeedback.of(Feedback.JSON_PREVIEW, true, behavior.getSerializationType(), name, GsonProvider.prettyGson().toJson(shown), null);
        }

        return RecipeExporterRegistry.get()
                .getExporter(behavior.getSerializationType())
                .addRecipe(recipeJson);
    }
}
