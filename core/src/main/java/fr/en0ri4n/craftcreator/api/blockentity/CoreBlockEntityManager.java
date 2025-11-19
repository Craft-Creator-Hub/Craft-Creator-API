package fr.en0ri4n.craftcreator.api.blockentity;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Manager for core block-entity types and behaviors.
 * - registerDefinition(...) for core definitions
 * - registerBehavior(...) to bind behavior id -> implementation
 * - create(...) to produce a CoreBlockEntity instance for a given type
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreBlockEntityManager {

    private static final CoreBlockEntityManager INSTANCE = new CoreBlockEntityManager();

    private final Map<String, CoreBlockEntityDefinition> definitions = new HashMap<>();
    private final Map<String, Supplier<BlockEntityBehavior>> behaviorRegistry = new HashMap<>();
    private boolean locked = false;

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
        BlockEntityBehavior behavior = behaviorRegistry.get(typeId.toString()).get();
        if (def == null) throw new IllegalArgumentException("Unknown block-entity type: " + typeId);

        return new CoreBlockEntity(typeId, behavior, def.getInventorySize());
    }

    /**
     * Lock manager so no further registrations happen (call at platform init end).
     */
    public synchronized void lock() {
        locked = true;
    }
}