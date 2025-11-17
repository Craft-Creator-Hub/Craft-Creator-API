package fr.en0ri4n.craftcreator.api.init;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Central manager that stores core block/item definitions.
 *
 * Core modules (or data providers) call registerBlock/registerItem/registerBlockItem during static initialization
 * or setup; later the platform calls runRegistrations(adapter) to perform actual registration.
 */
public final class InitManager {

    private static final InitManager INSTANCE = new InitManager();

    private final List<CoreBlockDef> blocks = new ArrayList<>();
    private final List<CoreItemDef> items = new ArrayList<>();
    private final List<CoreBlockItemDef> blockItemDefs = new ArrayList<>();
    private boolean locked = false;

    private InitManager() {}

    public static InitManager get() {
        return INSTANCE;
    }

    /**
     * Register a core block definition. Must be called before runRegistrations.
     */
    public synchronized void registerBlock(CoreBlockDef def) {
        checkNotLocked();
        Objects.requireNonNull(def, "def");
        blocks.add(def);
    }

    /**
     * Register a core item definition. Must be called before runRegistrations.
     */
    public synchronized void registerItem(CoreItemDef def) {
        checkNotLocked();
        Objects.requireNonNull(def, "def");
        items.add(def);
    }

    /**
     * Register a combined block + block-item definition. Must be called before runRegistrations.
     */
    public synchronized void registerBlockItem(CoreBlockItemDef def) {
        checkNotLocked();
        Objects.requireNonNull(def, "def");
        blockItemDefs.add(def);
    }

    /**
     * Read-only view of registered block definitions.
     */
    public synchronized List<CoreBlockDef> getBlocks() {
        return List.copyOf(blocks);
    }

    /**
     * Read-only view of registered item definitions.
     */
    public synchronized List<CoreItemDef> getItems() {
        return List.copyOf(items);
    }

    /**
     * Read-only view of registered block/item combined definitions.
     */
    public synchronized List<CoreBlockItemDef> getBlockItemDefs() {
        return List.copyOf(blockItemDefs);
    }

    private void checkNotLocked() {
        if (locked) throw new IllegalStateException("InitManager is locked; cannot register new defs after runRegistrations");
    }

    /**
     * Perform platform registration using the provided adapter.
     * After this call the manager is locked and no further core registrations are allowed.
     *
     * Implementations of RegistryAdapter must be safe to call at the platform's appropriate lifecycle moment.
     */
    public synchronized void runRegistrations(RegistryAdapter adapter) {
        Objects.requireNonNull(adapter, "adapter");
        if (locked) throw new IllegalStateException("runRegistrations already executed");

        // First register explicit combined block+item definitions (adapter may handle them specially).
        for (CoreBlockItemDef bid : new ArrayList<>(blockItemDefs)) {
            adapter.registerBlockItem(bid);
        }

        adapter.registerMenus();

        adapter.registerBlockEntities();

        locked = true;
    }
}