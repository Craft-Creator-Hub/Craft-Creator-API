package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;

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
//    void registerBlock(CoreBlockDef block);

    /**
     * Register a single item definition on the platform.
     */
//    void registerItem(CoreItemDef item);

    /**
     * Register all menus (containers) used by the mod.
     */
    void registerMenus();

    /**
     * Convenience: register a block and its block-item (if present) in a single step.
     * Platform adapters may implement this by registering the block and then creating
     * registering the item that wraps the block, or by delegating to registerBlock/registerItem.
     */
    void registerBlockItem(CoreBlockItemDef blockItemDef);

    /**
     * Register all block-entities used by the mod.
     */
    void registerBlockEntities();

    /**
     * Register all network packets used by the mod.
     */
    void registerPackets();
}