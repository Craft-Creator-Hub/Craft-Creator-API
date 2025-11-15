package fr.en0ri4n.craftcreator.api;

import fr.en0ri4n.craftcreator.utils.Identifier;

public abstract class CCReferences
{
    public static final String MOD_ID = "craftcreator";

    protected abstract String getModName();
    protected abstract String getModVersion();
    protected abstract Identifier getTranslation(String key);
}
