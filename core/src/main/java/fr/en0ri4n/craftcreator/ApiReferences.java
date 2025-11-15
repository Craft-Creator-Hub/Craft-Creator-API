package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.utils.Identifier;

public class ApiReferences extends CCReferences
{
    public static final String MOD_NAME = "Craft-Creator-API";
    public static final String VERSION = "1.0.0";

    @Override
    public String getModName()
    {
        return MOD_NAME;
    }

    @Override
    public String getModVersion()
    {
        return VERSION;
    }

    @Override
    public Identifier getTranslation(String key)
    {
        return null;
    }
}
