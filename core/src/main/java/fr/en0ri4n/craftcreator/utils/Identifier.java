package fr.en0ri4n.craftcreator.utils;

import fr.en0ri4n.craftcreator.api.CCReferences;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Identifier
{
    private final String namespace;
    private final String path;

    public static Identifier from(String namespace, String path)
    {
        return new Identifier(namespace, path);
    }

    public static Identifier from(String fullIdentifier)
    {
        String[] parts = fullIdentifier.split(":");
        if(parts.length != 2) throw new IllegalArgumentException("Invalid identifier: " + fullIdentifier);
        return new Identifier(parts[0], parts[1]);
    }

    /**
     * Creates an Identifier with the mod's namespace.
     * @param path the path of the identifier
     * @return the Identifier
     */
    public static Identifier fromMod(String path)
    {
        return new Identifier(CCReferences.MOD_ID, path);
    }

    @Override
    public String toString()
    {
        return this.namespace + ":" + this.path;
    }
}
