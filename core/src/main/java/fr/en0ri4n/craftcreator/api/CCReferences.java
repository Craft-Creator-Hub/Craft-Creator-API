package fr.en0ri4n.craftcreator.api;

import fr.en0ri4n.craftcreator.utils.Identifier;

public abstract class CCReferences
{
    public static final String MOD_ID = "craftcreator";

    public abstract String getModName();
    public abstract String getModVersion();
    public abstract Identifier getTranslation(String key);
}
