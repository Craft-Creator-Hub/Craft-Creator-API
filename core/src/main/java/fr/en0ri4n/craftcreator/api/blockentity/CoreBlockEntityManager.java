package fr.en0ri4n.craftcreator.api.blockentity;

import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Manager for core block-entity types and behaviors.
 * - registerDefinition(...) for core definitions
 * - registerBehavior(...) to bind behavior id -> implementation
 * - create(...) to produce a CoreBlockEntity instance for a given type
 */
public final class CoreBlockEntityManager {

    private static final CoreBlockEntityManager INSTANCE = new CoreBlockEntityManager();

    private final Map<String, CoreBlockEntityDefinition> definitions = new HashMap<>();
    private final Map<String, Supplier<BlockEntityBehavior>> behaviorRegistry = new HashMap<>();
    private boolean locked = false;

    private CoreBlockEntityManager() {}

    public static CoreBlockEntityManager get() { return INSTANCE; }

    public synchronized void registerDefinition(CoreBlockEntityDefinition def) {
        checkNotLocked();
        definitions.put(def.getId().toString(), def);
    }

    public synchronized void registerBehavior(Identifier id, Supplier<BlockEntityBehavior> behaviorFactory) {
        checkNotLocked();
        behaviorRegistry.put(id.toString(), behaviorFactory);
    }

    public CoreBlockEntityDefinition getDefinition(Identifier id) {
        return definitions.get(id.toString());
    }

    public Supplier<BlockEntityBehavior> getBehavior(Identifier id) {
        return behaviorRegistry.get(id.toString());
    }

    private void checkNotLocked() {
        if (locked) throw new IllegalStateException("CoreBlockEntityManager locked");
    }

    /**
     * Create a new CoreBlockEntity instance from a registered definition.
     */
    public CoreBlockEntity create(Identifier typeId) {
        CoreBlockEntityDefinition def = definitions.get(typeId.toString());
        if (def == null) throw new IllegalArgumentException("Unknown block-entity type: " + typeId);
        CoreBlockEntity entity = new CoreBlockEntity(typeId, def.getInventorySize());

        // instantiate and attach behaviors by resolving their suppliers
        for (Identifier bId : def.getBehaviors()) {
            Supplier<BlockEntityBehavior> sup = behaviorRegistry.get(bId.toString());
            if (sup != null) {
                BlockEntityBehavior beh = sup.get();
                try {
                    // call lifecycle load later from platform via context
                    // we store behavior in extraData keyed by behavior id if needed
                    // For simplicity behaviors can query manager.getBehaviorRegistry again if needed
                } catch (Exception ignored) {}
            }
        }

        return entity;
    }

    /**
     * Lock manager so no further registrations happen (call at platform init end).
     */
    public synchronized void lock() {
        locked = true;
    }
}