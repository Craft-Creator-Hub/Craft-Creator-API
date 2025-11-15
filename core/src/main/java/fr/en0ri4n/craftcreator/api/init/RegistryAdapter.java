package fr.en0ri4n.craftcreator.api.init;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;

/**
 * Platform adapter contract used by core to ask the loader to register items/blocks.
 *
 * Implement this on each platform (Forge/Fabric).
 * adapter.registerX(...) should perform the platform registration or schedule it on the platform thread.
 */
public interface RegistryAdapter {

    /**
     * Register a single block definition on the platform.
     * Implementations should ensure the block ends up in the corresponding game registry.
     */
    void registerBlock(CoreBlockDef block);

    /**
     * Register a single item definition on the platform.
     */
    void registerItem(CoreItemDef item);

    /**
     * Convenience: register a block and its block-item (if present) in a single step.
     * Platform adapters may implement this by registering the block and then creating
     * registering the item that wraps the block, or by delegating to registerBlock/registerItem.
     */
    default void registerBlockItem(CoreBlockItemDef blockItemDef) {
        registerBlock(blockItemDef.getBlock());
        if (blockItemDef.hasItem()) {
            registerItem(blockItemDef.getItem());
        }
    }
}