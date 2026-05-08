package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.item.ItemStackAdapter;
import fr.en0ri4n.craftcreator.api.item.tag.TagProvider;
import fr.en0ri4n.craftcreator.api.mod.SupportedModLoader;
import fr.en0ri4n.craftcreator.api.mod.SupportedVersion;
import fr.en0ri4n.craftcreator.api.translations.TextComponentProvider;

public interface Platform {
    SupportedModLoader getLoader();          // "Forge", "Fabric", "NeoForge", etc.
    SupportedVersion getMinecraftVersion();    // "1.18.2", "1.20.1", etc.

    boolean isClient();
    boolean isDedicatedServer();

    LoggerFacade getLogger();
    PathsProvider getPaths();
    LoaderServices getServices();

    IdentifierAdapter<?> getIdentifierAdapter();

    UiAdapter<?> getUiAdapter();

    RegistryAdapter getRegistryAdapter();

    BlockShapeAdapter<?> getBlockShapeAdapter();

    FacingAdapter<?> getFacingAdapter();

    RenderAdapter getRenderAdapter();

    ItemStackAdapter<?> getItemStackAdapter();

    BlockPosAdapter<?> getBlockPosAdapter();

    NetworkInteractionAdapter<?> getNetworkInteractionAdapter();

    TagProvider getTagProvider();

    TextComponentProvider<?> getTextComponentProvider();
}
