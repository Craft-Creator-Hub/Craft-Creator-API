package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.platform.ContainerUiAdapter;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ForgeContainerUiAdapter implements ContainerUiAdapter {

    @Override
    public void openContainer(ContainerModel model) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        mc.tell(() -> {
            // In real code you’d need a proper MenuType and window id, plus networking.
            int windowId = 10; //player.nextContainerCounter(); // or similar
            var menu = new ForgeRecipeCreatorMenu(windowId, player.getInventory(), model);
            var screen = new ForgeRecipeCreatorScreen(menu, player.getInventory(),
                    new net.minecraft.network.chat.TextComponent("Craft Creator"), model);
            mc.setScreen(screen);
        });
    }
}