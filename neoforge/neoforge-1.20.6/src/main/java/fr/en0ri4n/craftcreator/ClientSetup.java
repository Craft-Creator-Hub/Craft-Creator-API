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
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = References.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    private static KeyMapping openManagementScreenKey;

    /**
     * Menu screens are registered with their own event since NeoForge 20.6.
     */
    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(ForgeRegistryAdapter.RECIPE_CREATOR_MENU.get(), ForgeRecipeCreatorScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {

        // Block models can have transparent pixels (flowers, glass, saw blades)
        ForgeRegistryAdapter.get().getRegisteredBlocks().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout()));

    }

    /**
     * Key mappings are registered with their own event since 1.19.
     */
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
    {
        openManagementScreenKey = new KeyMapping(
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getName(),
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getKeyCode(),
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getCategory());
        event.register(openManagementScreenKey);
    }

    @EventBusSubscriber(modid = References.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class KeyBindings
    {
        @SubscribeEvent
        public static void onKeyPressed(ClientTickEvent.Post event)
        {
            // Key presses are only counted while no screen is open, so typing in a field never triggers it.
            // consumeClick also honors the key chosen in the controls menu.
            if(openManagementScreenKey == null || Minecraft.getInstance().screen != null) return;

            if(openManagementScreenKey.consumeClick())
                InitManager.OPEN_MANAGEMENT_SCREEN_KEYBIND.getOnPress().run();
        }
    }

    @EventBusSubscriber(modid = References.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class InventoryScreenHooks
    {
        @SubscribeEvent
        public static void onTooltip(ItemTooltipEvent event)
        {
            if(event.getEntity() == null) return;

            if(event.getEntity().containerMenu instanceof ForgeRecipeCreatorMenu menu)
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
                            event.getToolTip().add(Component.literal("Tag: " + tagId.toString()));
                        }
                    }
                }
            }
        }
    }


}
