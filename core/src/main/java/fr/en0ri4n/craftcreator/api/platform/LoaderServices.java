package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.mod.SupportedMods;

public interface LoaderServices {
    boolean isModLoaded(String modId);
    boolean IsModLoaded(SupportedMods mod);
}