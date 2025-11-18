package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.api.init.InitManager;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(References.MOD_ID)
public class CraftCreator
{
    public static final Logger LOGGER = LoggerFactory.getLogger(CraftCreator.class);
    private static CraftCreator instance;

    public CraftCreator() throws CraftCreatorException {
        instance = this;

        CraftCreatorAPI.get().initialize(Forge1182Platform.get(), new References());

        ForgeRegistryAdapter.get().registerDefferredRegisters(FMLJavaModLoadingContext.get().getModEventBus());
        // Now run core-registered definitions through the adapter.
        // This will call every adapter.registerBlockItem/Menus/Packets/... which
        // register entries with the DeferredRegister instances above.
        //
        // Important: runRegistrations must be called after the DeferredRegister objects
        // have been created and registered on the mod event bus (we do that above).
        try {
            InitManager.get().runRegistrations();
            LOGGER.info("InitManager registrations submitted to DeferredRegister");
        } catch (Exception e) {
            LOGGER.error("Failed to run InitManager registrations", e);
        }
    }

    public static CraftCreator get()
    {
        return instance;
    }
}