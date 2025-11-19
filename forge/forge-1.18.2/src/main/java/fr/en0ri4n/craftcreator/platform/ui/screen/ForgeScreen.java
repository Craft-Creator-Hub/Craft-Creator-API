package fr.en0ri4n.craftcreator.platform.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class ForgeScreen extends Screen
{
    private final CoreScreenDefinition screenDefinition;

    public ForgeScreen(CoreScreenDefinition<?> screenDefinition)
    {
        super(new TextComponent(screenDefinition.getTitle()));
        this.screenDefinition = screenDefinition;
    }

    @Override
    public void renderBackground(PoseStack pPoseStack)
    {
        ForgeRenderContext ctx = new ForgeRenderContext(pPoseStack, Minecraft.getInstance().renderBuffers().bufferSource(), 0.0f);
        screenDefinition.renderBackground(
                ctx,
                (this.width - screenDefinition.getBackgroundTextureSize().getFirstValue()) / 2,
                (this.height - screenDefinition.getBackgroundTextureSize().getSecondValue()) / 2,
                this.width,
                this.height);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick)
    {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }
}
