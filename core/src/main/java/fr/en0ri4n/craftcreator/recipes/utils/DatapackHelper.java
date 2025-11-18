package fr.en0ri4n.craftcreator.recipes.utils;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.platform.Platform;
import fr.en0ri4n.craftcreator.api.recipe.RecipeTypeKey;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;

public final class DatapackHelper {

    private DatapackHelper() {}

    /**
     * Get the output file path of the recipe in the Craft Creator data folder.
     * Example: <dataDir>/<recipePath>_from_<typePath>.json
     *
     * dataDir is provided by Platform.getPaths().getDataDirectory().
     */
    public static Path getOutputFile(RecipeTypeKey type, Identifier output) {
        Platform platform = CraftCreatorAPI.get().getPlatform();

        Path baseDir = platform.getPaths().getDataDirectory();
        ensureDirectory(baseDir);

        String fileName = buildFileName(type, output);
        return baseDir.resolve(fileName);
    }

    private static void ensureDirectory(Path dir) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (Exception e) {
            CraftCreatorAPI.get().getPlatform().getLogger()
                    .error("Failed to create Craft Creator data directory: " + dir, e);
        }
    }

    private static String buildFileName(RecipeTypeKey type, Identifier output) {
        // e.g. "iron_ingot_from_furnace.json"
        String outputPath = output.getPath();
        String typePath = type.getId().getPath();
        return outputPath + "_from_" + typePath + ".json";
    }
}