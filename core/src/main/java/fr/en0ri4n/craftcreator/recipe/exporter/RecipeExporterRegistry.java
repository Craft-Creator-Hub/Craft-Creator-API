package fr.en0ri4n.craftcreator.recipe.exporter;

import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import lombok.Getter;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
public class RecipeExporterRegistry
{
    private static final RecipeExporterRegistry INSTANCE = new RecipeExporterRegistry();
    public static RecipeExporterRegistry get() { return INSTANCE; }

    private final Map<SupportedRecipeExporter, ModRecipeExporter> recipeExporters = new HashMap<>();

    private void registerExporter(SupportedRecipeExporter mod, ModRecipeExporter exporter)
    {
        recipeExporters.put(mod, exporter);
    }

    public ModRecipeExporter getExporter(SupportedRecipeExporter mod)
    {
        return recipeExporters.get(mod);
    }

    /**
     * Load all registered exporters<br>
     * Platforms should call this method after world loading is complete to ensure all recipes are loaded correctly.
     */
    public void loadAll(Path worldPath) {
        for (ModRecipeExporter exporter : recipeExporters.values()) {
            exporter.load(worldPath);
        }
    }

    public void unloadAll()
    {
        for (ModRecipeExporter exporter : recipeExporters.values()) {
            exporter.unload();
        }
    }

    public static void registerAll()
    {
        get().registerExporter(SupportedRecipeExporter.MINECRAFT_DATAPACK, new DatapackRecipeExporter());
        get().registerExporter(SupportedRecipeExporter.KUBE_JS, new KubeJsRecipeExporter());
    }
}
