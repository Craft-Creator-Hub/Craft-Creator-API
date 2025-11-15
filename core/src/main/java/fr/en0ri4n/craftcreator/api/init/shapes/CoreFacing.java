package fr.en0ri4n.craftcreator.api.init.shapes;

/**
 * Loader-agnostic facing enumeration used by CoreDirectionalVoxelShape.
 * Mirrors typical Minecraft Direction values but stays independent from platform.
 */
public enum CoreFacing {
    NORTH, EAST, SOUTH, WEST, UP, DOWN
}