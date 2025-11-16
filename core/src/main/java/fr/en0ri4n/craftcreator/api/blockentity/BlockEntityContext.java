package fr.en0ri4n.craftcreator.api.blockentity;

import fr.en0ri4n.craftcreator.utils.Identifier;
import java.util.Optional;

/**
 * Platform-agnostic context passed to BlockEntityBehavior hooks.
 * Platform adapters implement this to expose minimal operations back to behavior code.
 */
public interface BlockEntityContext {

    boolean isClient();

    /**
     * Block position as a generic string (platform may expose a BlockPos string like "x,y,z")
     * or you may add dedicated accessors in platform adapter.
     */
    String getPosAsString();

    /**
     * Request the containing block to be marked dirty / saved.
     */
    void markDirty();

    /**
     * Send block update notification so clients re-render.
     */
    void sendBlockUpdate();

    /**
     * Open a container/screen for the given core element (optional).
     * Platform adapter decides how to implement (NetworkHooks on Forge).
     */
    void openContainer(Identifier containerId);

    /**
     * Optional: obtain an interacting player's identifier if available.
     */
    Optional<String> getInteractingPlayer();
}