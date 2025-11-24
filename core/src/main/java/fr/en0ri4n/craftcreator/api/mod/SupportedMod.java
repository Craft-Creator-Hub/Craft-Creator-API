package fr.en0ri4n.craftcreator.api.mod;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SupportedMod
{
    MINECRAFT("minecraft", new SupportedModLoader[] {SupportedModLoader.FORGE, SupportedModLoader.FABRIC, SupportedModLoader.NEOFORGE}),

    KUBEJS("kubejs", new SupportedModLoader[] {SupportedModLoader.FORGE, SupportedModLoader.FABRIC, SupportedModLoader.NEOFORGE}),

    BOTANIA("botania", new SupportedModLoader[] {SupportedModLoader.FORGE, SupportedModLoader.FABRIC}),
    THERMAL("thermal", new SupportedModLoader[] {SupportedModLoader.FORGE}),
    CREATE("create", new SupportedModLoader[] {SupportedModLoader.FORGE}),;

    private final String modId;
    private final SupportedModLoader[] modLoaders;

    public static SupportedMod getMod(String modId)
    {
        return Arrays.stream(values()).filter(mod -> mod.getModId().equals(modId)).findFirst().orElse(null);
    }

    public static List<SupportedMod> getSupportedLoadedMods()
    {
        return Arrays.stream(SupportedMod.values())
                .filter(mod -> CraftCreatorAPI.get().getPlatform().getServices().IsModLoaded(mod))
                .collect(Collectors.toList());
    }

    /**
     * Check if KubeJS is loaded
     *
     * @return True if KubeJS is loaded, false otherwise
     */
    public static boolean isKubeJSLoaded()
    {
        return CraftCreatorAPI.get().getPlatform().getServices().IsModLoaded(SupportedMod.KUBEJS);
    }
}
