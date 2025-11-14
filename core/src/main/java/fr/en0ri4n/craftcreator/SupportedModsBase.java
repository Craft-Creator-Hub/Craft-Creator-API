package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

public abstract class SupportedModsBase
{
    public abstract boolean isModLoaded(String modId);

    public abstract List<Identifier> getSupportedRecipeTypes();
}
