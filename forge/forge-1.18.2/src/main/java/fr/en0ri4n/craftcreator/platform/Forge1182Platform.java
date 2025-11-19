package fr.en0ri4n.craftcreator.platform;

import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.platform.RegistryAdapter;
import fr.en0ri4n.craftcreator.api.item.ItemStackAdapter;
import fr.en0ri4n.craftcreator.api.mod.SupportedModLoaders;
import fr.en0ri4n.craftcreator.api.mod.SupportedMods;
import fr.en0ri4n.craftcreator.api.platform.*;
import fr.en0ri4n.craftcreator.platform.adapters.*;
import fr.en0ri4n.craftcreator.platform.item.ForgeItemStackAdapter;
import fr.en0ri4n.craftcreator.platform.item.ForgeTagProvider;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

public class Forge1182Platform implements Platform {

    private static final Forge1182Platform INSTANCE = new Forge1182Platform();
    public static Forge1182Platform get() { return INSTANCE; }

    private final LoggerFacade loggerFacade = new LoggerFacade() {
        @Override public void info(String msg)  { CraftCreator.LOGGER.info(msg); }
        @Override public void warn(String msg)  { CraftCreator.LOGGER.warn(msg); }
        @Override public void error(String msg) { CraftCreator.LOGGER.error(msg); }
        @Override public void error(String msg, Throwable t) { CraftCreator.LOGGER.error(msg, t); }
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

    private final UiAdapter uiAdapter = new ForgeUiAdapter();

    private final RegistryAdapter registryAdapter = new ForgeRegistryAdapter();

    private final BlockShapeAdapter<VoxelShape> blockShapeAdapter = new ForgeBlockShapeAdapter();

    private final FacingAdapter<Direction> facingAdapter = new ForgeFacingAdapter();

    private final RenderAdapter renderAdapter = new ForgeRenderAdapter();

    private final BlockPosAdapter<BlockPos> blockPosAdapter = new BlockPosAdapter<>() {
        @Override
        public CoreBlockPos toCore(BlockPos loaderPos) {
            return new CoreBlockPos(loaderPos.getX(), loaderPos.getY(), loaderPos.getZ());
        }

        @Override
        public BlockPos fromCore(CoreBlockPos corePos) {
            return new BlockPos(corePos.getX(), corePos.getY(), corePos.getZ());
        }
    };

    private final NetworkInteractionAdapter<ServerPlayer> dataUpdateAdapter = new ForgeNetworkInteractionAdapter();

    private final ForgeTagProvider tagProvider = new ForgeTagProvider();

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
    public IdentifierAdapter<ResourceLocation> getIdentifierAdapter() {
        return identifierAdapter;
    }

    @Override
    public UiAdapter getUiAdapter() {
        return uiAdapter;
    }

    @Override
    public RegistryAdapter getRegistryAdapter()
    {
        return registryAdapter;
    }

    @Override
    public BlockShapeAdapter<VoxelShape> getBlockShapeAdapter()
    {
        return blockShapeAdapter;
    }

    @Override
    public FacingAdapter<Direction> getFacingAdapter()
    {
        return facingAdapter;
    }

    @Override
    public RenderAdapter getRenderAdapter()
    {
        return renderAdapter;
    }

    @Override
    public ItemStackAdapter<ItemStack> getItemStackAdapter()
    {
        return ForgeItemStackAdapter.get();
    }

    @Override
    public BlockPosAdapter<BlockPos> getBlockPosAdapter()
    {
        return blockPosAdapter;
    }

    @Override
    public NetworkInteractionAdapter<ServerPlayer> getNetworkInteractionAdapter()
    {
        return dataUpdateAdapter;
    }

    @Override
    public ForgeTagProvider getTagProvider()
    {
        return tagProvider;
    }
}