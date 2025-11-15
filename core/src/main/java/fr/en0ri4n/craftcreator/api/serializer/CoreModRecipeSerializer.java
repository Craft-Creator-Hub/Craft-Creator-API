package fr.en0ri4n.craftcreator.api.serializer;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedMods;
import fr.en0ri4n.craftcreator.api.platform.Platform;
import fr.en0ri4n.craftcreator.api.recipe.RecipeTypeKey;
import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.recipes.utils.DatapackHelper;
import fr.en0ri4n.craftcreator.serialize.GsonProvider;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

/**
 * Loader-agnostic base for mod recipe serializers.
 *
 * It knows how to:
 * - build base JSON structures
 * - write recipes to datapacks and/or KubeJS
 * using only core abstractions.
 */
@Getter
public abstract class CoreModRecipeSerializer {

    /** Namespace / mod id this serializer targets, e.g. "minecraft", "create", "botania". */
    private final SupportedMods mod;

    /** Where to output recipes by default. */
    private final Set<RecipeOutputTarget> defaultTargets;

    protected CoreModRecipeSerializer(SupportedMods mod, Set<RecipeOutputTarget> defaultTargets) {
        this.mod = mod;
        this.defaultTargets = EnumSet.copyOf(defaultTargets);
    }

    protected CoreModRecipeSerializer(SupportedMods mod, RecipeOutputTarget singleTarget) {
        this(mod, EnumSet.of(singleTarget));
    }

    /**
     * Create a base JSON object with the given recipe type identifier.
     * This maps to the datapack "type" field.
     */
    protected JsonObject createBaseJson(Identifier recipeTypeId) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", recipeTypeId.toString());
        return obj;
    }

    /**
     * Helper: {"item": "..."} or {"tag": "..."}.
     */
    protected JsonObject singletonItemJsonObject(String key, String value) {
        JsonObject obj = new JsonObject();
        obj.addProperty(key, value);
        return obj;
    }

    /**
     * Helper for entries that already know whether they're a tag.
     */
    protected JsonObject singletonItemJsonObject(boolean isTag, Identifier id) {
        return singletonItemJsonObject(isTag ? "tag" : "item", id.toString());
    }

    /**
     * Main entry point: write a recipe JSON for a given type/output id to the configured targets.
     */
    protected void addRecipe(JsonObject json,
                             Identifier recipeTypeId,
                             Identifier outputId,
                             RecipeInfos infos) {
        addRecipe(json, recipeTypeId, outputId, infos, defaultTargets);
    }

    /**
     * Same as above, but with explicit targets (e.g. datapack-only, kubejs-only, etc.).
     */
    protected void addRecipe(JsonObject json,
                             Identifier recipeTypeId,
                             Identifier outputId,
                             RecipeInfos infos,
                             Set<RecipeOutputTarget> targets) {

        Platform platform = CraftCreatorAPI.getInstance().getPlatform();

        if (targets.contains(RecipeOutputTarget.DATAPACK)) {
            writeDatapackRecipe(platform, json, recipeTypeId, outputId);
        }

        if (targets.contains(RecipeOutputTarget.KUBEJS)) {
            writeKubeJsRecipe(platform, json, recipeTypeId, outputId, infos);
        }
    }

    /* -------------------------------------------------------------------------
     * Internal writers (still core-only)
     * ---------------------------------------------------------------------- */

    private void writeDatapackRecipe(Platform platform,
                                     JsonObject json,
                                     Identifier recipeTypeId,
                                     Identifier outputId) {
        Path out = DatapackHelper.getOutputFile(new RecipeTypeKey(recipeTypeId), outputId);
        try {
            Files.createDirectories(out.getParent());
            String contents = GsonProvider.gson().toJson(json);
            Files.writeString(out, contents, StandardCharsets.UTF_8);
            platform.getLogger().info("Wrote datapack recipe: " + out);
        } catch (IOException e) {
            platform.getLogger().error("Failed to write datapack recipe " + outputId +
                    " for type " + recipeTypeId + " to " + out, e);
        }
    }

    /**
     * Writes a KubeJS script file or appends to an existing one.
     * The exact format is up to you; here's a simple event-based example.
     */
    private void writeKubeJsRecipe(Platform platform,
                                   JsonObject json,
                                   Identifier recipeTypeId,
                                   Identifier outputId,
                                   RecipeInfos infos) {
        // KubeJS usually lives under <gameDir>/kubejs/server_scripts
        Path kubejsDir = platform.getPaths().getGameDirectory()
                .resolve("kubejs")
                .resolve("server_scripts");

        String fileName = "craftcreator_" + mod + "_recipes.js";
        Path scriptFile = kubejsDir.resolve(fileName);

        try {
            Files.createDirectories(scriptFile.getParent());

            String jsSnippet = buildKubeJsSnippet(json, recipeTypeId, outputId, infos);
            Files.writeString(scriptFile, jsSnippet, StandardCharsets.UTF_8,
                    Files.exists(scriptFile)
                            ? java.nio.file.StandardOpenOption.APPEND
                            : java.nio.file.StandardOpenOption.CREATE);

            platform.getLogger().info("Wrote KubeJS recipe snippet to: " + scriptFile);
        } catch (IOException e) {
            platform.getLogger().error("Failed to write KubeJS recipe " + outputId +
                    " for type " + recipeTypeId + " to " + scriptFile, e);
        }
    }

    /**
     * Turn the JSON recipe into a KubeJS snippet.
     * This is minimal and you can evolve it later.
     */
    private String buildKubeJsSnippet(JsonObject json,
                                      Identifier recipeTypeId,
                                      Identifier outputId,
                                      RecipeInfos infos) {
        String jsonString = GsonProvider.gson().toJson(json);

        // Very simple example using onEvent('recipes', ...)
        return """
               onEvent('recipes', event => {
                 event.custom(%s);
               });
               
               """.formatted(jsonString);
    }
}