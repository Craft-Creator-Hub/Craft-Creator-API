package fr.en0ri4n.craftcreator.impl.block;

import fr.en0ri4n.craftcreator.utils.CCShape;
import fr.en0ri4n.craftcreator.utils.HasRegistryName;

/**
 * Represents a block in the Craft Creator API.
 */
public interface CCBlock extends HasRegistryName
{
    CCShape getShape();
    boolean isFullCube();
    default boolean hasFacing() {
        return false;
    }
    default boolean onUse() {
        return true;
    }
}
