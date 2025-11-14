package fr.en0ri4n.craftcreator.api.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedMods;
import fr.en0ri4n.craftcreator.api.recipe.model.CraftingGrid;
import fr.en0ri4n.craftcreator.api.recipe.serialize.CraftingTableRecipeSerializer;
import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.serialize.GsonProvider;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loader-agnostic manager for KubeJS recipe actions (add/remove/modify).
 * Writes snippets into kubejs/server_scripts/craftcreator_&lt;modId&gt;_recipes.js.
 *
 * All filesystem locations come from Platform paths. No Forge/Fabric classes here.
 */
@RequiredArgsConstructor
public class KubeJsActions {

    /**
     * Mod whose recipes we are manipulating.
     * The modId is used in script filenames and logging.
     */
    private final SupportedMods mod;

    /**
     * Get a KubeJsActions manager for the given mod.
     */
    public static KubeJsActions forMod(SupportedMods mod) {
        return new KubeJsActions(mod);
    }

    /* -------------------------------------------------------------------------
     * Recipe removal
     * ---------------------------------------------------------------------- */

    /**
     * Remove a single recipe by id.
     *
     * Generates:
     * onEvent('recipes', event => {
     *   event.remove({ id: 'namespace:path' });
     * });
     */
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

    /* -------------------------------------------------------------------------
     * Recipe addition
     * ---------------------------------------------------------------------- */

    /**
     * Add a custom crafting table recipe via KubeJS, using the core crafting serializer.
     *
     * For shaped recipes, provide a 3x3 CraftingGrid.
     * For shapeless recipes, provide a MultiInput.
     *
     * The resulting JSON is fed into event.custom(...).
     */
    public void addCraftingRecipe(Identifier recipeId,
                                  boolean shaped,
                                  CraftingGrid shapedGrid,
                                  RecipeEntry.MultiInput shapelessInputs,
                                  RecipeEntry result,
                                  RecipeInfos infos) {

        JsonObject recipeJson;
        if (shaped) {
            if (shapedGrid == null) {
                throw new IllegalArgumentException("CraftingGrid must not be null for shaped recipes");
            }
            recipeJson = CraftingTableRecipeSerializer.shaped(
                    result.getId(), result.getCount(), shapedGrid
            );
        } else {
            if (shapelessInputs == null) {
                throw new IllegalArgumentException("shapelessInputs must not be null for shapeless recipes");
            }
            recipeJson = CraftingTableRecipeSerializer.shapeless(
                    result.getId(), result.getCount(), shapelessInputs
            );
        }

        // KubeJS convention: ensure id is set
        recipeJson.addProperty("id", recipeId.toString());

        // If you want to embed extra info flags from RecipeInfos, you can do it here.
        // For now, infos is accepted for future use but not written.

        addCustomRecipe(recipeJson);
    }

    /**
     * Add an arbitrary custom JSON recipe via KubeJS.
     *
     * Generates:
     * onEvent('recipes', event => {
     *   event.custom(<json>);
     * });
     */
    public void addCustomRecipe(JsonObject recipeJson) {
        String snippet = """
            onEvent('recipes', event => {
              event.custom(%s);
            });
            
            """.formatted(toJson(recipeJson));

        appendToScript(snippet);
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
        var platform = CraftCreatorAPI.getInstance().getPlatform();

        // kubejs/server_scripts/craftcreator_<modId>_recipes.js
        String modId = mod.getModId();
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
        return GsonProvider.gson().toJson(element);
    }

    private static String indent(String text, int spaces) {
        String prefix = " ".repeat(spaces);
        return text.lines()
                .map(line -> line.isBlank() ? "" : prefix + line)
                .reduce((a, b) -> a + System.lineSeparator() + b)
                .orElse("");
    }
}