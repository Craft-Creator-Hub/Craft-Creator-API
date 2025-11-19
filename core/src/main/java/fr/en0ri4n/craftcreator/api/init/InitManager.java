package fr.en0ri4n.craftcreator.api.init;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.platform.RegistryAdapter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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

        new ArrayList<>(blockItemDefs).forEach(adapter::registerBlockItem);

        adapter.registerMenus();
        adapter.registerBlockEntities();
        adapter.registerPackets();

        locked = true;
    }
}