package fr.en0ri4n.craftcreator.recipe.exporter;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.ApiReferences;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializer;
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
            try(InputStream inputStream = CraftCreatorAPI.class.getResourceAsStream("/assets/craftcreator/presets/pack.mcmeta"))
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

        if(doesRecipeExist(recipeJson.get("id").getAsString()))
        {
            CraftCreatorAPI.LOGGER.error("A recipe with the same id already exists in the datapack: " + recipeJson.get("id").getAsString());
            return RecipeRequestFeedback.of(Feedback.ALREADY_EXISTS, false);
        }

        String recipeName = recipeJson.has("name") ? recipeJson.get("name").getAsString() : recipeJson.get("id").getAsString();
        Path recipeFilePath = recipesPath.resolve(recipeName + ".json");

        try
        {
            Files.writeString(recipeFilePath, GsonProvider.prettyGson().toJson(recipeJson));
            CraftCreatorAPI.LOGGER.info("Added custom recipe to datapack: " + recipeName);

            JsonObject cleanedRecipeJson = recipeJson.deepCopy();
            cleanedRecipeJson.remove("id");
            cleanedRecipeJson.remove("name"); // Remove id & name from feedback as it's not needed for the user and can be confusing

            return RecipeRequestFeedback.of(Feedback.DATAPACK_ADDED, true, SupportedRecipeExporter.MINECRAFT_DATAPACK, recipeName, GsonProvider.prettyGson().toJson(cleanedRecipeJson), recipeFilePath.toString());
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

        // Files are named after the recipe name, so look for the file holding this id
        File[] recipeFiles = recipesPath.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if(recipeFiles == null) return RecipeRequestFeedback.of(Feedback.DATAPACK_FILE_ERROR, false);

        for(File recipeFile : recipeFiles)
        {
            JsonObject recipeJson = readRecipeJson(recipeFile);
            if(recipeJson == null || !recipeJson.has("id") || !recipeJson.get("id").getAsString().equals(id.getPath())) continue;

            try
            {
                Files.delete(recipeFile.toPath());
                CraftCreatorAPI.LOGGER.info("Removed custom recipe from datapack: " + id);
                return RecipeRequestFeedback.of(Feedback.DATAPACK_REMOVED, true);
            }
            catch(IOException e)
            {
                CraftCreatorAPI.LOGGER.error("Failed to remove custom recipe from datapack: " + id, e);
                return RecipeRequestFeedback.of(Feedback.DATAPACK_FILE_ERROR, false);
            }
        }

        CraftCreatorAPI.LOGGER.warn("No custom recipe found in datapack with id: " + id);
        return RecipeRequestFeedback.of(Feedback.DATAPACK_FILE_ERROR, false);
    }

    private static JsonObject readRecipeJson(File recipeFile)
    {
        try
        {
            return GsonProvider.compactGson().fromJson(Files.readString(recipeFile.toPath()), JsonObject.class);
        }
        catch(Exception e)
        {
            CraftCreatorAPI.LOGGER.error("Failed to read recipe file: " + recipeFile.getName(), e);
            return null;
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
            JsonObject recipeJson = readRecipeJson(recipeFile);
            if(recipeJson == null || !recipeJson.has("type")) continue;

            RecipeSerializer serializer = RecipeSerializerRegistry.get().getByRecipeTypeId(Identifier.from(recipeJson.get("type").getAsString()));
            if(serializer == null)
            {
                CraftCreatorAPI.LOGGER.warn("No serializer for recipe type of file: " + recipeFile.getName());
                continue;
            }

            Recipe recipe = serializer.deserializeToRecipe(recipeJson);
            if(recipe != Recipe.EMPTY) loadedRecipes.add(recipe);
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
            CraftCreatorAPI.LOGGER.error("Datapack recipes path is not initialized. Cannot add custom recipe.");
            return true;
        }

        return false;
    }
}
