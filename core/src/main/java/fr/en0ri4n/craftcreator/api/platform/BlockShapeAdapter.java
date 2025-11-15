package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.init.shapes.CoreVoxelShape;

import java.util.Objects;

/**
 * Core adapter interface to convert CoreVoxelShape into a platform-specific shape.
 * Platform modules (Forge/Fabric) implement this and expose conversion helpers.
 *
 * Example: Forge implementation will be BlockShapeAdapter<VoxelShape>.
 *
 * Note: keep this interface in core so core code can be written against it if you need
 * to convert shapes in a platform-agnostic way at runtime.
 */
public interface BlockShapeAdapter<T> {

    /**
     * Convert a CoreVoxelShape into the platform-specific shape type T.
     */
    T toPlatformShape(CoreVoxelShape shape);

    /**
     * Simple null-safe wrapper.
     */
    default T toPlatformShapeSafe(CoreVoxelShape shape) {
        Objects.requireNonNull(shape, "shape");
        return toPlatformShape(shape);
    }
}