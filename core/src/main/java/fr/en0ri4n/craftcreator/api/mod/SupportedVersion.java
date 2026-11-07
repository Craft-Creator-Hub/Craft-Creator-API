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
    // In release order: isGreaterOrEquals compares positions
    V1_18_2("1.18.2"),
    V1_19_4("1.19.4"),
    V1_20_1("1.20.1"),
    V1_20_4("1.20.4"),
    V1_20_6("1.20.6"),
    V1_21_2("1.21.2"), // No version for craft creator but one major change in recipes
    V1_21_10("1.21.10"),
    V1_21_11("1.21.11"),
    V26_1_2("26.1.2"),
    V26_2("26.2"),
    V26_3("26.3");

    private final String versionName;

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
