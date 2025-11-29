package fr.en0ri4n.craftcreator;

import com.mojang.blaze3d.platform.InputConstants;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.impl.InitManager;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeRecipeCreatorScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CCReferences.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        MenuScreens.register(ForgeRegistryAdapter.RECIPE_CREATOR_MENU.get(), ForgeRecipeCreatorScreen::new);

        ClientRegistry.registerKeyBinding(new KeyMapping(
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getName(),
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getKeyCode(),
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getCategory()));
    }

    @Mod.EventBusSubscriber(modid = CCReferences.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class KeyBindings
    {
        @SubscribeEvent
        public static void onKeyPressed(TickEvent.ClientTickEvent event)
        {
            if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getKeyCode()))
            {
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getOnPress().run();
            }
        }
    }


}