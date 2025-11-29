package fr.en0ri4n.craftcreator.recipe.exporter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedMod;
import fr.en0ri4n.craftcreator.api.platform.Platform;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.serialize.GsonProvider;
import fr.en0ri4n.craftcreator.utils.Feedback;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Loader-agnostic manager for KubeJS recipe actions (add/remove/modify).
 * Writes snippets into kubejs/server_scripts/craftcreator_&lt;modId&gt;_recipes.js.
 *
 * All filesystem locations come from Platform paths. No Forge/Fabric classes here.
 */
public class KubeJsRecipeExporter extends AdvancedModRecipeExporter
{
    @Override
    public void loadInternal()
    {
        if(!CraftCreatorAPI.get().getPlatform().getServices().IsModLoaded(SupportedMod.KUBEJS))
        {
            CraftCreatorAPI.get().getPlatform().getLogger().warn("KubeJS not detected, not loading KubeJsRecipeExporter");
            return;
        }
    }

    /**
     * Remove a single recipe by id.
     *
     * Generates:
     * onEvent('recipes', event => {
     *   event.remove({ id: 'namespace:path' });
     * });
     */
    @Override
    public void removeRecipe(Identifier recipeId) {
        JsonObject removeObj = new JsonObject();
        removeObj.addProperty("id", recipeId.toString());

        String snippet = """
            onEvent('recipes', event => {
              event.remove(%s);
            });
            
            """.formatted(toJson(removeObj));

        appendToScript(snippet);
    }

    /**
     * Remove all recipes of a given type.
     *
     * Generates:
     * onEvent('recipes', event => {
     *   event.remove({ type: 'namespace:path' });
     * });
     */
    @Override
    public void removeAllOfType(Identifier recipeTypeId) {
        JsonObject removeObj = new JsonObject();
        removeObj.addProperty("type", recipeTypeId.toString());

        String snippet = """
            onEvent('recipes', event => {
              event.remove(%s);
            });
            
            """.formatted(toJson(removeObj));

        appendToScript(snippet);
    }

    @Override
    public List<Recipe> getRemovedRecipes()
    {
        return List.of();
    }

    /* -------------------------------------------------------------------------
     * Recipe addition
     * ---------------------------------------------------------------------- */

    /**
     * Add an arbitrary custom JSON recipe via KubeJS.
     *
     * Generates:
     * onEvent('recipes', event => {
     *   event.custom(<json>);
     * });
     */
    @Override
    public RecipeRequestFeedback addRecipe(JsonObject recipeJson) {
        String snippet = """
            onEvent('recipes', event => {
              event.custom(%s);
            });
            
            """.formatted(toJson(recipeJson));

        appendToScript(snippet);
        return RecipeRequestFeedback.of(Feedback.KUBEJS_ADDED, true);
    }

    @Override
    public RecipeRequestFeedback removeAddedRecipe(Identifier id)
    {
        return null;
    }

    @Override
    public List<Recipe> getRecipes()
    {
        return List.of();
    }

    /* -------------------------------------------------------------------------
     * Recipe modification
     * ---------------------------------------------------------------------- */

    /**
     * Low-level hook for complex modifications where JSON alone isn't enough.
     *
     * @param body JS code to run, with parameter name "event".
     *
     * Example:
     *   modifyWithJs(\"\"\"
     *     event.forEachRecipe({ type: 'minecraft:smelting' }, r => {
     *       r.experience = 0.5;
     *     });
     *   \"\"\");
     */
    public void modifyWithJs(String body) {
        String snippet = """
            onEvent('recipes', event => {
            %s
            });
            
            """.formatted(indent(body, 2));

        appendToScript(snippet);
    }

    /**
     * Modify a single recipe by id, wrapping a small JS body.
     *
     * @param recipeId id of the recipe to target.
     * @param body body of a function that receives "recipe".
     *
     * Example body:
     *   "recipe.result = {item: 'minecraft:diamond'};"
     *
     * Generates:
     * onEvent('recipes', event => {
     *   event.forEachRecipe({ id: 'mod:id' }, recipe => {
     *     <body>
     *   });
     * });
     */
    public void modifyRecipe(Identifier recipeId, String body) {
        JsonObject filter = new JsonObject();
        filter.addProperty("id", recipeId.toString());

        String snippet = """
            onEvent('recipes', event => {
              event.forEachRecipe(%s, recipe => {
            %s
              });
            });
            
            """.formatted(
                toJson(filter),
                indent(body, 4)
        );

        appendToScript(snippet);
    }

    /* -------------------------------------------------------------------------
     * Internal helpers
     * ---------------------------------------------------------------------- */

    private void appendToScript(String snippet) {
        Platform platform = CraftCreatorAPI.get().getPlatform();

        // kubejs/server_scripts/craftcreator_<modId>_recipes.js
        String modId = "getMod().getModId()";
        Path kubejsDir = platform.getPaths().getGameDirectory()
                .resolve("kubejs")
                .resolve("server_scripts");

        String fileName = "craftcreator_" + modId + "_recipes.js";
        Path scriptFile = kubejsDir.resolve(fileName);

        try {
            Files.createDirectories(scriptFile.getParent());
            Files.writeString(
                    scriptFile,
                    snippet,
                    StandardCharsets.UTF_8,
                    Files.exists(scriptFile)
                            ? java.nio.file.StandardOpenOption.APPEND
                            : java.nio.file.StandardOpenOption.CREATE
            );
            platform.getLogger().info("Wrote KubeJS snippet for " + modId + " to " + scriptFile);
        } catch (IOException e) {
            platform.getLogger().error("Failed to write KubeJS snippet for " + modId + " to " + scriptFile, e);
        }
    }

    private String toJson(JsonElement element) {
        return GsonProvider.prettyGson().toJson(element);
    }

    private static String indent(String text, int spaces) {
        String prefix = " ".repeat(spaces);
        return text.lines()
                .map(line -> line.isBlank() ? "" : prefix + line)
                .reduce((a, b) -> a + System.lineSeparator() + b)
                .orElse("");
    }
}