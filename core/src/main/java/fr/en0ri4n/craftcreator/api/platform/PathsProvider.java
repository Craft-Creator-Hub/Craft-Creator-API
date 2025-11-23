package fr.en0ri4n.craftcreator.api.platform;

import java.nio.file.Path;

public interface PathsProvider {
    Path getGameDirectory();   // .minecraft / instance root
    Path getConfigDirectory(); // config/
    Path getDataDirectory();   // where you currently use FMLLoader.getGamePath()/"Craft-Creator"

    Path getWorldDirectory(String worldName);  // saves/<current world>/
}
