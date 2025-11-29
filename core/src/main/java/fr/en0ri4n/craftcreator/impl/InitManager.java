package fr.en0ri4n.craftcreator.impl;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.impl.model.screen.RecipeManagementScreen;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreator;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.FacingType;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreFacing;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreShapes;
import fr.en0ri4n.craftcreator.api.platform.RegistryAdapter;
import fr.en0ri4n.craftcreator.utils.CoreKeybind;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Central manager that stores core block/item definitions.
 * <p>
 * Core modules (or data providers) call registerBlockItem during static initialization
 * or setup; later the platform calls runRegistrations(adapter) to perform actual registration.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InitManager
{
    private static final InitManager INSTANCE = new InitManager();
    public static InitManager get() {
        return INSTANCE;
    }

    // Keybind
    public static final CoreKeybind OPEN_MANAGEMENT_SCREEN_KEYBIND = new CoreKeybind("key.open_recipe_management_screen",
                                                                                     75,
                                                                                     "key.category.name",
                                                                                     () -> CraftCreatorAPI.get().getPlatform().getUiAdapter().openScreen(new RecipeManagementScreen()));


    public static final CoreBlockDef CRAFTING_TABLE_RECIPE_CREATOR_BLOCK = CoreBlockDef.builder(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR_ID)
                                                                                       .facing(FacingType.HORIZONTAL)
                                                                                       .facingShapes(Map.of(
                                                                                               CoreFacing.WEST, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_WEST,
                                                                                               CoreFacing.EAST, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_EAST,
                                                                                               CoreFacing.NORTH, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_NORTH,
                                                                                               CoreFacing.SOUTH, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_SOUTH
                                                                                       ))
                                                                                       .build();

    public static final CoreBlockDef FURNACE_RECIPE_CREATOR_BLOCK = CoreBlockDef.builder(RecipeCreators.FURNACE_RECIPE_CREATOR_ID)
                                                                                .facing(FacingType.HORIZONTAL)
                                                                                .build();

    private final List<CoreBlockItemDef> blockItemDefs = new ArrayList<>();
    private boolean locked = false;

    public void registerAll()
    {
        CraftCreatorAPI.LOGGER.info("Registering core blocks and items...");

        for(RecipeCreator<?> recipeCreator : RecipeCreators.ALL_RECIPE_CREATORS)
        {
            registerBlockAndItem(CoreBlockItemDef.of(recipeCreator.getRecipeCreatorBlock(), itemOf(recipeCreator.getId())));
        }
    }

    private CoreItemDef itemOf(Identifier id) {
        return CoreItemDef.builder(id)
                .maxStackSize(10)
                .build();
    }

    /**
     * Register a combined block + block-item definition. Must be called before runRegistrations.
     */
    public synchronized void registerBlockAndItem(CoreBlockItemDef def) {
        checkNotLocked();
        Objects.requireNonNull(def, "CoreBlockItemDef cannot be null");
        blockItemDefs.add(def);
    }

    private void checkNotLocked() {
        if (locked) throw new IllegalStateException("InitManager is locked; cannot register new defs after runRegistrations");
    }

    /**
     * Perform platform registration using the provided adapter.
     * After this call the manager is locked and no further core registrations are allowed.
     * <p>
     * Implementations of RegistryAdapter must be safe to call at the platform's appropriate lifecycle moment.
     */
    public synchronized void runRegistrations() {
        RegistryAdapter adapter = CraftCreatorAPI.get().getPlatform().getRegistryAdapter();

        Objects.requireNonNull(adapter, "adapter");
        if (locked) throw new IllegalStateException("runRegistrations already executed");

        CraftCreatorAPI.LOGGER.info("InitManager registrations started...");

        new ArrayList<>(blockItemDefs).forEach(adapter::registerBlockItem);

        adapter.registerMenus();
        adapter.registerBlockEntities();
        adapter.registerPackets();

        CraftCreatorAPI.LOGGER.info("InitManager registrations completed.");

        locked = true;
    }
}