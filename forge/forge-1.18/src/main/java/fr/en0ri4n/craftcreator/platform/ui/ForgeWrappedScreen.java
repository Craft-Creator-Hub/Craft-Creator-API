package fr.en0ri4n.craftcreator.platform.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.ScreenRenderer;
import fr.en0ri4n.craftcreator.api.ui.VirtualScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class ForgeWrappedScreen extends Screen {

    private final ScreenRenderer renderer;

    public ForgeWrappedScreen(ScreenRenderer renderer) {
        super(new TextComponent("Craft Creator"));
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        VirtualScreen vs = new ForgeVirtualScreen(this.width, this.height, poseStack);
        renderer.render(vs, mouseX, mouseY, partialTicks);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}