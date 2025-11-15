package fr.en0ri4n.craftcreator.platform;

import fr.en0ri4n.craftcreator.api.mod.SupportedModLoaders;
import fr.en0ri4n.craftcreator.api.mod.SupportedMods;
import fr.en0ri4n.craftcreator.api.platform.*;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class ForgePlatform implements Platform {

    private static final ForgePlatform instance = new ForgePlatform();
    public static ForgePlatform getInstance() {
        return instance;
    }

    private final Logger logger = LogManager.getLogger("CraftCreator");

    private final LoggerFacade loggerFacade = new LoggerFacade() {
        @Override public void info(String msg)  { logger.info(msg); }
        @Override public void warn(String msg)  { logger.warn(msg); }
        @Override public void error(String msg) { logger.error(msg); }
        @Override public void error(String msg, Throwable t) { logger.error(msg, t); }
    };

    private final PathsProvider paths = new PathsProvider() {
        @Override
        public Path getGameDirectory() {
            return FMLLoader.getGamePath();
        }

        @Override
        public Path getConfigDirectory() {
            return FMLLoader.getGamePath().resolve("config");
        }

        @Override
        public Path getDataDirectory() {
            return FMLLoader.getGamePath().resolve("Craft-Creator");
        }
    };

    private final LoaderServices services = new LoaderServices() {
        @Override
        public boolean isModLoaded(String modId) {
            return ModList.get().isLoaded(modId);
        }

        @Override
        public boolean IsModLoaded(SupportedMods mod) {
            return ModList.get().isLoaded(mod.getModId());
        }
    };

    private final IdentifierAdapter<ResourceLocation> identifierAdapter = new IdentifierAdapter<>() {
        @Override
        public Identifier toCore(ResourceLocation loaderId) {
            return new Identifier(loaderId.getNamespace(), loaderId.getPath());
        }

        @Override
        public ResourceLocation fromCore(Identifier coreId) {
            return new ResourceLocation(coreId.getNamespace(), coreId.getPath());
        }
    };

    private final BlockAdapter<Block> blockAdapter = new ForgeBlockAdapter();

    private final UiAdapter uiAdapter = new ForgeUiAdapter();

    private final ContainerUiAdapter containerUiAdapter = new ForgeContainerUiAdapter();

    @Override
    public SupportedModLoaders getLoader() {
        return SupportedModLoaders.FORGE;
    }

    @Override
    public String getMinecraftVersion() {
        return FMLLoader.versionInfo().mcVersion();
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public boolean isDedicatedServer() {
        return !isClient();
    }

    @Override
    public LoggerFacade getLogger() {
        return loggerFacade;
    }

    @Override
    public PathsProvider getPaths() {
        return paths;
    }

    @Override
    public LoaderServices getServices() {
        return services;
    }

    @Override
    public BlockAdapter<Block> getBlockAdapter() {
        return blockAdapter;
    }

    @Override
    public IdentifierAdapter<ResourceLocation> getIdentifierAdapter() {
        return identifierAdapter;
    }

    @Override
    public ContainerUiAdapter getContainerUiAdapter()
    {
        return containerUiAdapter;
    }

    @Override
    public UiAdapter getUiAdapter() {
        return uiAdapter;
    }
}