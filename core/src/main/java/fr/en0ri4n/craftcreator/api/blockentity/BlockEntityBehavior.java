package fr.en0ri4n.craftcreator.api.blockentity;

import com.google.gson.JsonObject;

/**
 * Lifecycle hooks you can register on core block-entity definitions.
 * Implementations can be provided in core for pure logic or supplied by platform
 * if platform interaction is required.
 */
public interface BlockEntityBehavior {

    /**
     * Called when the entity is loaded (after construction / deserialization).
     */
    default void onLoad(CoreBlockEntity entity, BlockEntityContext ctx) {}

    /**
     * Called when the block is removed/broken.
     */
    default void onRemove(CoreBlockEntity entity, BlockEntityContext ctx) {}

    /**
     * Called when a player interacts with the block (right-click).
     * Return true if the interaction was handled and no default behavior is required.
     */
    default boolean onInteract(CoreBlockEntity entity, BlockEntityContext ctx) { return false; }

    /**
     * Called when the entity must serialize any custom data to JSON (for persistence).
     */
    default void save(CoreBlockEntity entity, JsonObject out) {}

    /**
     * Called when the entity must restore any custom data from JSON (for persistence).
     */
    default void load(CoreBlockEntity entity, JsonObject in) {}
}