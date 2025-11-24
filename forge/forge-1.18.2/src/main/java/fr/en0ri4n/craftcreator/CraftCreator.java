package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.impl.InitManager;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.recipe.exporter.RecipeExporterRegistry;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(References.MOD_ID)
@Mod.EventBusSubscriber(modid = References.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CraftCreator
{
    public static final Logger LOGGER = LoggerFactory.getLogger(CraftCreator.class);
    private static CraftCreator instance;

    public CraftCreator() throws CraftCreatorException
    {
        instance = this;

        CraftCreatorAPI.get().initialize(Forge1182Platform.get(), new References());

        ForgeRegistryAdapter.get().registerDefferredRegisters(FMLJavaModLoadingContext.get().getModEventBus());
        // Now run core-registered definitions through the adapter.
        // This will call every adapter.registerBlockItem/Menus/Packets/... which
        // register entries with the DeferredRegister instances above.
        //
        // Important: runRegistrations must be called after the DeferredRegister objects
        // have been created and registered on the mod event bus (we do that above).
        try
        {
            InitManager.get().runRegistrations();
        }
        catch(Exception e)
        {
            LOGGER.error("Failed to run InitManager registrations", e);
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if(event.getWorld().isClientSide() || event.getWorld().getServer() == null)
            return;

        RecipeExporterRegistry.get().loadAll(event.getWorld().getServer().getWorldPath(LevelResource.ROOT));
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event)
    {
        if(event.getWorld().isClientSide() || event.getWorld().getServer() == null)
            return;

        RecipeExporterRegistry.get().unloadAll();
    }

    public static CraftCreator get()
    {
        return instance;
    }
}