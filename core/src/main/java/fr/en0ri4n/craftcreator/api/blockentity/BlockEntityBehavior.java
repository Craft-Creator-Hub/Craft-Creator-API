package fr.en0ri4n.craftcreator.api.blockentity;

import com.google.gson.JsonObject;

/**
 * Class representing a behavior that can be attached to a block entity.<br>
 * Behaviors have custom deserialization/serialization logic.<br>
 * They are used as storage for custom data to simplify client/server synchronization and persistence
 * as the client screen and server block entity are synchronized automatically.
 */
public interface BlockEntityBehavior {

    /**
     * Called when the entity must serialize any custom data to JSON (for persistence).
     */
    default void save(CoreBlockEntity entity, JsonObject out) {}

    /**
     * Called when the entity must restore any custom data from JSON (for persistence).
     */
    default void load(CoreBlockEntity entity, JsonObject in) {}
}