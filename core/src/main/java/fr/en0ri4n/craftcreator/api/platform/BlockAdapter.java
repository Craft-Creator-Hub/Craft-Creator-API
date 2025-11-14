package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.block.CCBlock;
import fr.en0ri4n.craftcreator.utils.Identifier;

/**
 * Conversion between core CCBlock/Identifier and loader-native block objects.
 * T is the platform block type (e.g., net.minecraft.world.level.block.Block in Forge/Fabric).
 */
public interface BlockAdapter<T> {
    CCBlock toCore(Identifier id);

    /**
     * Look up the platform block by id. May return null if not found.
     */
    T toPlatform(CCBlock block);

    /**
     * Resolve a block from a platform object to CCBlock.
     */
    CCBlock fromPlatform(T platformBlock);
}