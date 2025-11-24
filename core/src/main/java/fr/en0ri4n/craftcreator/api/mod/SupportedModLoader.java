package fr.en0ri4n.craftcreator.api.mod;

import lombok.Getter;

@Getter
public enum SupportedModLoader
{
    FORGE("forge", "Forge"),
    FABRIC("fabric", "Fabric"),
    NEOFORGE("neoforge", "NeoForge"),;

    private final String modLoaderId;
    private final String modLoaderName;

    SupportedModLoader(String modLoaderId, String modLoaderName) {
        this.modLoaderId = modLoaderId;
        this.modLoaderName = modLoaderName;
    }
}
