package fr.en0ri4n.craftcreator.api.mod;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum SupportedVersion
{
    // In release order: isGreaterOrEquals compares positions.
    // The data pack format is the pack_format of the generated datapack; 0 means not known yet.
    V1_18_2("1.18.2", 9),
    V1_19_2("1.19.2", 10),
    V1_19_4("1.19.4", 12),
    V1_20_1("1.20.1", 15),
    V1_20_4("1.20.4", 26),
    V1_20_6("1.20.6", 41),
    V1_21_1("1.21.1", 48),
    V1_21_2("1.21.2", 57), // No version for craft creator but one major change in recipes
    V1_21_10("1.21.10", 88),
    V1_21_11("1.21.11", 0),
    V26_1_2("26.1.2", 0),
    V26_2("26.2", 0),
    V26_3("26.3", 0);

    private final String versionName;
    private final int dataPackFormat;

    @Override
    public String toString()
    {
        return versionName;
    }

    public static boolean isGreaterOrEquals(SupportedVersion version)
    {
        List<SupportedVersion> versions = Arrays.stream(SupportedVersion.values()).toList();
        SupportedVersion currentVersion = CraftCreatorAPI.get().getPlatform().getMinecraftVersion();
        return versions.indexOf(currentVersion) >= versions.indexOf(version);
    }
}
