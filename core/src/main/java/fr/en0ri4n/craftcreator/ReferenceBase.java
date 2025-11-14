package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.utils.Identifier;

public interface ReferenceBase
{
    String getModId();
    String getModName();
    String getModVersion();
    Identifier getTranslation(String key);
}
