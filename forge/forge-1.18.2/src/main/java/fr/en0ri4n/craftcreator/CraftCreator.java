package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.api.init.InitManager;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(References.MOD_ID)
public class CraftCreator
{
    public static final Logger LOGGER = LoggerFactory.getLogger(CraftCreator.class);
    private static CraftCreator instance;
    private final Forge1182Platform platform;

    public CraftCreator() throws CraftCreatorException {
        instance = this;

        CraftCreatorAPI.getInstance().initialize(platform = new Forge1182Platform(), new References());

        ForgeRegistryAdapter adapter = ForgeRegistryAdapter.getInstance();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        adapter.getBlockDeferredRegister().register(modEventBus);
        adapter.getItemDeferredRegister().register(modEventBus);
        // Now run core-registered definitions through the adapter.
        // This will call adapter.registerBlock/registerItem/registerBlockItem which
        // register entries with the DeferredRegister instances above.
        //
        // Important: runRegistrations must be called after the DeferredRegister objects
        // have been created and registered on the mod event bus (we do that above).
        try {
            InitManager.get().runRegistrations(adapter);
            LOGGER.info("InitManager registrations submitted to DeferredRegister");
        } catch (Exception e) {
            LOGGER.error("Failed to run InitManager registrations", e);
        }
    }

    public static CraftCreator getInstance()
    {
        return instance;
    }

    public Forge1182Platform getPlatform()
    {
        return platform;
    }
}