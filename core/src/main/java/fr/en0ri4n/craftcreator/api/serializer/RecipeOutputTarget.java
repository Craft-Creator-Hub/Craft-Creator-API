package fr.en0ri4n.craftcreator.api.serializer;

/**
 * Where generated recipes should be written.
 */
public enum RecipeOutputTarget {
    DATAPACK,   // data/<namespace>/recipes/...
    KUBEJS      // kubejs/server_scripts/...
}