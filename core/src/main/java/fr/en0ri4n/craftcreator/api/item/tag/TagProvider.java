package fr.en0ri4n.craftcreator.api.item.tag;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

/**
 * Core API: given a CoreItemStack return a list of tag identifiers (strings)
 * that should be presented to the user for selection.
 * <p>
 * Implementations live in platform modules (Forge/Fabric) because they need
 * access to platform item stacks / registries / NBT.
 */
public interface TagProvider {
    /**
     * Return an ordered list of tag identifiers for the provided core item stack.
     * Never return null; return an empty list if no tags exist.
     */
    List<Identifier> getTags(CoreItemStack coreStack);
}