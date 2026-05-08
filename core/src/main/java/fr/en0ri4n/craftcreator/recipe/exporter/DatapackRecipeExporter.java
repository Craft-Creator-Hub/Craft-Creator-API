package fr.en0ri4n.craftcreator.recipe.exporter;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.ApiReferences;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializerRegistry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.serialize.GsonProvider;
import fr.en0ri4n.craftcreator.utils.Feedback;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DatapackRecipeExporter extends ModRecipeExporter
{
    private static final String DATAPACK_NAME = "craftcreator_generated";

    private Path datapackPath;
    private Path recipesPath;

    @Override
    public void loadInternal()
    {
        CraftCreatorAPI.LOGGER.info("Loading datapack recipes from world: " + getCurrentWorldPath().toString());
        createDatapackStructure();
    }

    private void createDatapackStructure()
    {
        datapackPath = getCurrentWorldPath().resolve("datapacks").resolve(DATAPACK_NAME);
        // Create folders and necessary files for a valid datapack
        File datapackDir = datapackPath.toFile();

        if(!datapackDir.exists()) datapackDir.mkdirs();

        // Create pack.mcmeta
        File packMcmeta = new File(datapackDir, "pack.mcmeta");

        if(!packMcmeta.exists())
        {
            // Get file from resources assets/craftcreator/pack.mcmeta
            try(InputStream inputStream = CraftCreatorAPI.class.getClassLoader().getResourceAsStream("/assets/craftcreator/presets/pack.mcmeta"))
            {
                if(inputStream == null) throw new IOException("pack.mcmeta resource not found");

                Files.copy(inputStream, packMcmeta.toPath());
            }
            catch(IOException e)
            {
                CraftCreatorAPI.LOGGER.error("Failed to create pack.mcmeta for datapack", e);
            }
        }

        // Create folders
        File dataDir = new File(datapackDir, "data/" + ApiReferences.MOD_ID + "/recipes");

        if(!dataDir.exists())
            dataDir.mkdirs();

        recipesPath = dataDir.toPath();

        CraftCreatorAPI.LOGGER.info("Created datapack structure at: " + datapackPath);
    }

    @Override
    public RecipeRequestFeedback addRecipe(JsonObject recipeJson)
    {
        if(arePathsInvalid())
            return RecipeRequestFeedback.of(Feedback.DATAPACK_PATH_INVALID, false);

        if(!recipeJson.has("type") || !recipeJson.has("id"))
        {
            CraftCreatorAPI.LOGGER.error("Invalid recipe JSON: missing 'type' or 'id' field.");
            return RecipeRequestFeedback.of(Feedback.INVALID_JSON_RECIPE, false);
        }

        String recipeName = recipeJson.has("name") ? recipeJson.get("name").getAsString() : recipeJson.get("id").getAsString();
        Path recipeFilePath = recipesPath.resolve(recipeName + ".json");

        try
        {
            Files.writeString(recipeFilePath, GsonProvider.prettyGson().toJson(recipeJson));
            CraftCreatorAPI.LOGGER.info("Added custom recipe to datapack: " + recipeName);
            return RecipeRequestFeedback.of(Feedback.DATAPACK_ADDED, true, recipeJson.get("name").getAsString(), GsonProvider.prettyGson().toJson(recipeJson), recipeFilePath.toString());
        }
        catch(IOException e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to write custom recipe to datapack: " + recipeName, e);
            return RecipeRequestFeedback.of(Feedback.DATAPACK_FILE_ERROR, false);
        }
    }

    @Override
    public RecipeRequestFeedback removeAddedRecipe(Identifier id)
    {
        if(arePathsInvalid())
            return RecipeRequestFeedback.of(Feedback.DATAPACK_PATH_INVALID, false);

        Path recipeFilePath = recipesPath.resolve(id.toString() + ".json");

        try
        {
            Files.deleteIfExists(recipeFilePath);
            CraftCreatorAPI.LOGGER.info("Removed custom recipe from datapack: " + id);
            return RecipeRequestFeedback.of(Feedback.DATAPACK_REMOVED, true);
        }
        catch(IOException e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to remove custom recipe from datapack: " + id, e);
            return RecipeRequestFeedback.of(Feedback.INVALID_JSON_RECIPE, false);
        }
    }

    @Override
    public List<Recipe> getRecipes()
    {
        File recipesDir = recipesPath.toFile();
        if(!recipesDir.exists() || !recipesDir.isDirectory())
        {
            CraftCreatorAPI.LOGGER.warn("Recipes directory does not exist: " + recipesPath.toString());
            return List.of();
        }

        File[] recipeFiles = recipesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if(recipeFiles == null) return List.of();

        List<Recipe> loadedRecipes = new ArrayList<>();

        for(File recipeFile : recipeFiles)
        {
            try
            {
                String content = Files.readString(recipeFile.toPath());
                JsonObject recipeJson = GsonProvider.compactGson().fromJson(content, JsonObject.class);

                String typeStr = recipeJson.get("type").getAsString();
                Recipe recipe = RecipeSerializerRegistry.get().getByRecipeTypeId(Identifier.from(typeStr)).deserializeToRecipe(recipeJson);
                loadedRecipes.add(recipe);
            }
            catch(Exception e)
            {
                CraftCreatorAPI.LOGGER.error("Failed to read recipe file: " + recipeFile.getName(), e);
            }
        }

        return loadedRecipes;
    }

    private boolean arePathsInvalid()
    {
        if(datapackPath == null)
        {
            CraftCreatorAPI.LOGGER.error("Datapack path is not initialized. Cannot add custom recipe.");
            return true;
        }

        if(recipesPath == null)
        {
            CraftCreatorAPI.LOGGER.error("Datapack path is not initialized. Cannot add custom recipe.");
            return true;
        }

        return false;
    }
}
