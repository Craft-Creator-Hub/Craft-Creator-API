package fr.en0ri4n.craftcreator.recipe;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.recipe.exporter.RecipeExporterRegistry;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializer;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializerRegistry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.utils.Feedback;
import fr.en0ri4n.craftcreator.utils.Identifier;

public class RecipeManager
{
    public static final RecipeManager INSTANCE = new RecipeManager();
    public static RecipeManager get() { return INSTANCE; }

    public RecipeRequestFeedback handleMakeRecipeRequest(CoreBlockEntity coreBlockEntity, Identifier containerId)
    {
        RecipeSerializer serializer = RecipeSerializerRegistry.get().getByRecipeTypeId(containerId);
        RecipeCreatorBlockEntityBehavior behavior = (RecipeCreatorBlockEntityBehavior) coreBlockEntity.getBehavior();

        if(!serializer.isBlockDataValid(coreBlockEntity))
            return RecipeRequestFeedback.of(Feedback.INVALID_BLOCK_DATA, false);

        JsonObject recipeJson = serializer.serialize(coreBlockEntity);

        return RecipeExporterRegistry.get()
                .getExporter(behavior.getSerializationType())
                .addRecipe(recipeJson);
    }
}
