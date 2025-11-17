package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.init.RegistryAdapter;
import fr.en0ri4n.craftcreator.api.mod.SupportedModLoaders;

public interface Platform {
    SupportedModLoaders getLoader();          // "Forge", "Fabric", "NeoForge", etc.
    String getMinecraftVersion();    // "1.18.2", "1.20.1", etc.

    boolean isClient();
    boolean isDedicatedServer();

    LoggerFacade getLogger();
    PathsProvider getPaths();
    LoaderServices getServices();

    BlockAdapter<?> getBlockAdapter();

    IdentifierAdapter<?> getIdentifierAdapter();

    ContainerUiAdapter getContainerUiAdapter();

    UiAdapter getUiAdapter();

    RegistryAdapter getRegistryAdapter();

    BlockShapeAdapter<?> getBlockShapeAdapter();

    FacingAdapter<?> getFacingAdapter();

    RenderAdapter getRenderAdapter();
}
