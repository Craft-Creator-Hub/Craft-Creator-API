package fr.en0ri4n.craftcreator.platform.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.VirtualScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class ForgeVirtualScreen implements VirtualScreen {

    private final int width;
    private final int height;
    private final PoseStack poseStack;
    private final Minecraft mc = Minecraft.getInstance();

    public ForgeVirtualScreen(int width, int height, PoseStack poseStack) {
        this.width = width;
        this.height = height;
        this.poseStack = poseStack;
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }

    @Override
    public void drawRect(int x, int y, int w, int h, int argb) {
        // use Forge 1.18 GUI helper:
        // fill(poseStack, x, y, x + w, y + h, argb);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y, x + w, y + h, argb);
    }

    @Override
    public void drawText(String text, int x, int y, int color) {
        Font font = mc.font;
        font.draw(poseStack, text, x, y, color);
    }

    @Override
    public void drawItem(String itemId, int x, int y) {
        // Later: resolve itemId -> ItemStack and use mc.getItemRenderer()
    }
}