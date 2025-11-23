package fr.en0ri4n.craftcreator.api.init;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.FacingType;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreFacing;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreShapes;
import fr.en0ri4n.craftcreator.api.platform.RegistryAdapter;
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
public class InitManager {

    private static final InitManager INSTANCE = new InitManager();
    public static InitManager get() {
        return INSTANCE;
    }

    private final List<CoreBlockItemDef> blockItemDefs = new ArrayList<>();
    private boolean locked = false;

    public void registerAll() {
        CraftCreatorAPI.LOGGER.info("Registering core blocks and items...");

        CoreBlockDef craftingTableBlock = CoreBlockDef.builder(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR)
                .facing(FacingType.HORIZONTAL)
                .facingShapes(Map.of(
                        CoreFacing.WEST, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_WEST,
                        CoreFacing.EAST, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_EAST,
                        CoreFacing.NORTH, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_NORTH,
                        CoreFacing.SOUTH, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_SOUTH
                ))
                .build();


        registerBlockItem(CoreBlockItemDef.of(craftingTableBlock, itemOf(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR)));
    }

    private CoreItemDef itemOf(Identifier id) {
        return CoreItemDef.builder(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR)
                .maxStackSize(10)
                .build();
    }

    /**
     * Register a combined block + block-item definition. Must be called before runRegistrations.
     */
    public synchronized void registerBlockItem(CoreBlockItemDef def) {
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