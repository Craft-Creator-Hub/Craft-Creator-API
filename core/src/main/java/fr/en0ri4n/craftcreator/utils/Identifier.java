package fr.en0ri4n.craftcreator.utils;

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

    @Override
    public String toString()
    {
        return this.namespace + ":" + this.path;
    }
}
