package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.impl.InitManager;
import fr.en0ri4n.craftcreator.platform.NeoForge1211Platform;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.recipe.exporter.RecipeExporterRegistry;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import fr.en0ri4n.craftcreator.platform.net.NetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(References.MOD_ID)
@EventBusSubscriber(modid = References.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CraftCreator
{
    public static final Logger LOGGER = LoggerFactory.getLogger(CraftCreator.class);
    private static CraftCreator instance;

    /**
     * NeoForge gives the mod event bus to the constructor.
     */
    public CraftCreator(IEventBus modBus) throws CraftCreatorException
    {
        instance = this;

        CraftCreatorAPI.get().initialize(NeoForge1211Platform.get(), new References());

        ForgeRegistryAdapter.get().registerDefferredRegisters(modBus);
        modBus.addListener(NetworkHandler::register);
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
    public static void onWorldLoad(LevelEvent.Load event)
    {
        if(event.getLevel().isClientSide() || event.getLevel().getServer() == null)
            return;

        RecipeExporterRegistry.get().loadAll(event.getLevel().getServer().getWorldPath(LevelResource.ROOT));
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event)
    {
        if(event.getLevel().isClientSide() || event.getLevel().getServer() == null)
            return;

        RecipeExporterRegistry.get().unloadAll();
    }

    public static CraftCreator get()
    {
        return instance;
    }
}