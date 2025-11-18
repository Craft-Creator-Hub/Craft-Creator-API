package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.impl.blockentity.definitions.CoreBlockEntityDefinitionsRegistrar;
import fr.en0ri4n.craftcreator.api.init.InitManager;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.FacingType;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreFacing;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreShapes;
import fr.en0ri4n.craftcreator.api.platform.Platform;
import fr.en0ri4n.craftcreator.api.recipe.serialize.RecipeInfosSerializer;
import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.impl.model.ContainerModels;
import fr.en0ri4n.craftcreator.serialize.SerializerRegistry;
import fr.en0ri4n.craftcreator.utils.CoreLogger;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
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

        CraftCreatorAPI.LOGGER.info("Initializing CraftCreatorAPI[%s][%s-%s]...".formatted(ApiReferences.VERSION, platform.getLoader().getModLoaderName(), platform.getMinecraftVersion()));

        // register serializers
        SerializerRegistry.register(RecipeInfos.class, new RecipeInfosSerializer());

        // Registrations
        registerBlockItems();

        // Register container models
        ContainerModels.get().registerAll();

        // register block-entity definitions
        CoreBlockEntityDefinitionsRegistrar.init();

        CraftCreatorAPI.LOGGER.info("CraftCreatorAPI[%s][%s-%s] initialized successfully.".formatted(ApiReferences.VERSION, platform.getLoader().getModLoaderName(), platform.getMinecraftVersion()));
    }

    private void registerBlockItems()
    {
        CraftCreatorAPI.LOGGER.info("Registering core blocks and items...");
        // during core static init or module setup
        InitManager.get().registerBlockItem(CoreBlockItemDef.of(
                CoreBlockDef.builder(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR)
                        .facing(FacingType.HORIZONTAL)
                        .facingShapes(Map.of(
                                CoreFacing.WEST, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_WEST,
                                CoreFacing.EAST, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_EAST,
                                CoreFacing.NORTH, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_NORTH,
                                CoreFacing.SOUTH, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_SOUTH
                        ))
                        .build(),
                CoreItemDef.builder(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR)
                        .maxStackSize(10)
                        .build()
        ));
    }
}
