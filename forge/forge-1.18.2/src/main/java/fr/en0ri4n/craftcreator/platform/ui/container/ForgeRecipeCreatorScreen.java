package fr.en0ri4n.craftcreator.platform.ui.container;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ForgeRecipeCreatorScreen extends AbstractContainerScreen<ForgeRecipeCreatorMenu> {

    private final ContainerModel model;

    public ForgeRecipeCreatorScreen(ForgeRecipeCreatorMenu menu, Inventory playerInv, Component title, ContainerModel model) {
        super(menu, playerInv, title);
        this.model = model;
        this.imageWidth = model.getLayout().getWidth();
        this.imageHeight = model.getLayout().getHeight();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        // draw background using layout info or textures
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }
}