package fr.en0ri4n.craftcreator.api.mod;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SupportedMods
{
    MINECRAFT("minecraft", new SupportedModLoaders[] {SupportedModLoaders.FORGE, SupportedModLoaders.FABRIC, SupportedModLoaders.NEOFORGE}),

    KUBEJS("kubejs", new SupportedModLoaders[] {SupportedModLoaders.FORGE, SupportedModLoaders.FABRIC, SupportedModLoaders.NEOFORGE}),

    BOTANIA("botania", new SupportedModLoaders[] {SupportedModLoaders.FORGE, SupportedModLoaders.FABRIC}),
    THERMAL("thermal", new SupportedModLoaders[] {SupportedModLoaders.FORGE}),
    CREATE("create", new SupportedModLoaders[] {SupportedModLoaders.FORGE}),;

    private final String modId;
    private final SupportedModLoaders[] modLoaders;

    public static SupportedMods getMod(String modId)
    {
        return Arrays.stream(values()).filter(mod -> mod.getModId().equals(modId)).findFirst().orElse(null);
    }

    public static List<SupportedMods> getSupportedLoadedMods()
    {
        return Arrays.stream(SupportedMods.values())
                .filter(mod -> CraftCreatorAPI.getInstance().getPlatform().getServices().IsModLoaded(mod))
                .collect(Collectors.toList());
    }

    /**
     * Check if KubeJS is loaded
     *
     * @return True if KubeJS is loaded, false otherwise
     */
    public static boolean isKubeJSLoaded()
    {
        return CraftCreatorAPI.getInstance().getPlatform().getServices().IsModLoaded(SupportedMods.KUBEJS);
    }
}
