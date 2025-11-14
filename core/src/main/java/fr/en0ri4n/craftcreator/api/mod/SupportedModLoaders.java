package fr.en0ri4n.craftcreator.api.mod;

import lombok.Getter;

public enum SupportedModLoaders {
    FORGE("forge"),
    FABRIC("fabric"),
    NEOFORGE("neoforge");

    @Getter
    private final String modLoaderId;

    SupportedModLoaders(String modLoaderId) {
        this.modLoaderId = modLoaderId;
    }
}
