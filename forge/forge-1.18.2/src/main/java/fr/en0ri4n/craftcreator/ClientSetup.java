package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.api.ui.screen.TaggableSlotsContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.InitManager;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.TaggableSlotsBlockEntityBehavior;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeRecipeCreatorScreen;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = References.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    private static KeyMapping openManagementScreenKey;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        MenuScreens.register(ForgeRegistryAdapter.RECIPE_CREATOR_MENU.get(), ForgeRecipeCreatorScreen::new);

        openManagementScreenKey = new KeyMapping(
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getName(),
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getKeyCode(),
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getCategory());
        ClientRegistry.registerKeyBinding(openManagementScreenKey);
    }

    @Mod.EventBusSubscriber(modid = References.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class KeyBindings
    {
        @SubscribeEvent
        public static void onKeyPressed(TickEvent.ClientTickEvent event)
        {
            // Key presses are only counted while no screen is open, so typing in a field never triggers it.
            // consumeClick also honors the key chosen in the controls menu.
            if(event.phase != TickEvent.Phase.END || openManagementScreenKey == null || Minecraft.getInstance().screen != null) return;

            if(openManagementScreenKey.consumeClick())
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getOnPress().run();
        }
    }

    @Mod.EventBusSubscriber(modid = References.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class InventoryScreenHooks
    {
        @SubscribeEvent
        public static void onTooltip(ItemTooltipEvent event)
        {
            if(event.getPlayer() == null) return;

            if(event.getPlayer().containerMenu instanceof ForgeRecipeCreatorMenu menu)
            {
                if(Minecraft.getInstance().screen instanceof ForgeRecipeCreatorScreen screen)
                {
                    Slot slot = screen.getSlotUnderMouse();
                    // Check if the slot belongs to our custom container and not the player inventory
                    if(slot != null && slot.container instanceof ForgeGenericBlockEntity)
                    {
                        TaggableSlotsContainerScreenDefinition<? extends TaggableSlotsBlockEntityBehavior> taggableSlotsContainerScreenDefinition =
                                (TaggableSlotsContainerScreenDefinition<? extends TaggableSlotsBlockEntityBehavior>) screen.getModel().getScreenDefinition();

                        Map<Integer, Identifier> tags = taggableSlotsContainerScreenDefinition.getScreenData().getBehavior().getTaggedSlots();

                        Optional<Integer> taggedSlot =  tags.keySet()
                            .stream()
                            .filter(index -> index == slot.index)
                            .findFirst();

                        if(taggedSlot.isPresent())
                        {
                            Identifier tagId = tags.get(taggedSlot.get());
                            event.getToolTip().add(new TextComponent("Tag: " + tagId.toString()));
                        }
                    }
                }
            }
        }
    }


}