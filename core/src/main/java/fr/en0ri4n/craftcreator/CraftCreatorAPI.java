package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.impl.InitManager;
import fr.en0ri4n.craftcreator.api.platform.Platform;
import fr.en0ri4n.craftcreator.impl.blockentity.definitions.CoreBlockEntityDefinitionsRegistrar;
import fr.en0ri4n.craftcreator.impl.model.ContainerModels;
import fr.en0ri4n.craftcreator.recipe.exporter.RecipeExporterRegistry;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializerRegistry;
import fr.en0ri4n.craftcreator.utils.CoreLogger;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CraftCreatorAPI {

    private static final CraftCreatorAPI INSTANCE = new CraftCreatorAPI();
    public static CraftCreatorAPI get() { return INSTANCE; }

    public static CoreLogger LOGGER = new CoreLogger();

    private static boolean initialized = false;

    private Platform platform;

    private CCReferences references;

    public void initialize(Platform platform, CCReferences references) throws CraftCreatorException {
        if (initialized)
            throw new CraftCreatorException("CraftCreatorAPI has already been initialized !");

        initialized = true;

        Objects.requireNonNull(platform, "Platform must not be null");
        this.platform = platform;

        Objects.requireNonNull(references, "References must not be null");
        this.references = references;

        // Initialize logger first
        platform.getLogger().createLogger(this.getClass());

        CraftCreatorAPI.LOGGER.info("Initializing CraftCreatorAPI[%s][%s-%s] using %s...".formatted(
                ApiReferences.VERSION,
                platform.getLoader().getModLoaderName(),
                platform.getMinecraftVersion(),
                platform.getClass().getName()));

        // register recipe serializers
        RecipeSerializerRegistry.registerAll();

        // register exporters
        RecipeExporterRegistry.registerAll();

        // Registrations of blocks/items definitions
        InitManager.get().registerAll();

        // register block-entity definitions
        CoreBlockEntityDefinitionsRegistrar.init();

        // Register container models
        ContainerModels.get().registerAll();

        CraftCreatorAPI.LOGGER.info("CraftCreatorAPI[%s][%s-%s] initialized successfully.".formatted(ApiReferences.VERSION, platform.getLoader().getModLoaderName(), platform.getMinecraftVersion()));
    }

    public static String translate(String key, Object... args) {
        return get().getPlatform().getTranslationProvider().translate(CCReferences.MOD_ID + "." + key, args);
    }
}
