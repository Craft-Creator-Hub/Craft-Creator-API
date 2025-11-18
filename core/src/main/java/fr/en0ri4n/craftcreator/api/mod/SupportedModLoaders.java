package fr.en0ri4n.craftcreator.api.mod;

import lombok.Getter;

@Getter
public enum SupportedModLoaders {
    FORGE("forge", "Forge"),
    FABRIC("fabric", "Fabric"),
    NEOFORGE("neoforge", "NeoForge"),;

    private final String modLoaderId;
    private final String modLoaderName;

    SupportedModLoaders(String modLoaderId, String modLoaderName) {
        this.modLoaderId = modLoaderId;
        this.modLoaderName = modLoaderName;
    }
}
