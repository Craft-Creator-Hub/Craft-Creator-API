package fr.en0ri4n.craftcreator.api.init.definitions;

/**
 * Orientation types for blocks. Adapter implementations decide exact mapping.
 */
public enum FacingType {
    NONE,        // block has no facing
    HORIZONTAL,  // rotates around vertical axis (north/east/south/west)
    VERTICAL,    // facing includes up/down (all 6 directions)
    PILLAR       // rotates around axis (X/Y/Z) like logs
}