package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.api.ClientUtils;
import fr.en0ri4n.craftcreator.commands.TestRecipesCommand;
import fr.en0ri4n.craftcreator.init.*;
import fr.en0ri4n.craftcreator.platform.ForgePlatform;
import fr.en0ri4n.craftcreator.screen.container.BotaniaRecipeCreatorScreen;
import fr.en0ri4n.craftcreator.screen.container.CreateRecipeCreatorScreen;
import fr.en0ri4n.craftcreator.screen.container.MinecraftRecipeCreatorScreen;
import fr.en0ri4n.craftcreator.screen.container.ThermalRecipeCreatorScreen;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(References.MOD_ID)
public class CraftCreator
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static MinecraftServer SERVER;

    private ForgePlatform platform;

    public CraftCreator() throws CraftCreatorException {
        platform = new ForgePlatform();

        CraftCreatorAPI.getInstance().initialize(platform, new References());
    }

    public void clientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ClientRegistry.registerKeyBinding(ClientUtils.KEY_OPEN_RECIPES_MENU);
            ClientRegistry.registerKeyBinding(ClientUtils.KEY_OPEN_TUTORIAL);

            ClientUtils.setDefaultBlockRender(InitBlocks.BOTANIA_RECIPE_CREATOR.get());
            ClientUtils.setDefaultBlockRender(InitBlocks.MINECRAFT_RECIPE_CREATOR.get());
            ClientUtils.setDefaultBlockRender(InitBlocks.THERMAL_RECIPE_CREATOR.get());
            ClientUtils.setDefaultBlockRender(InitBlocks.CREATE_RECIPE_CREATOR.get());

            ClientUtils.registerScreen(InitContainers.MINECRAFT_RECIPE_CREATOR.get(), MinecraftRecipeCreatorScreen::new);
            ClientUtils.registerScreen(InitContainers.BOTANIA_RECIPE_CREATOR.get(), BotaniaRecipeCreatorScreen::new);
            ClientUtils.registerScreen(InitContainers.THERMAL_RECIPE_CREATOR.get(), ThermalRecipeCreatorScreen::new);
            ClientUtils.registerScreen(InitContainers.CREATE_RECIPE_CREATOR.get(), CreateRecipeCreatorScreen::new);
        });
    }

    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event)
    {
        SERVER = event.getServer();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent e)
    {
        TestRecipesCommand.register(e.getDispatcher());
    }

    public static final CreativeModeTab CRAFT_CREATOR_TAB = new CreativeModeTab(References.MOD_ID)
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(InitItems.MINECRAFT_RECIPE_CREATOR.get());
        }
    };
}