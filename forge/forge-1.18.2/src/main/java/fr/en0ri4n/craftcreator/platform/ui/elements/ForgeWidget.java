package fr.en0ri4n.craftcreator.platform.ui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;

public class ForgeWidget extends AbstractWidget implements Widget
{
    private final CoreUiElement coreUiElement;

    public ForgeWidget(CoreUiElement coreUiElement)
    {
        super(coreUiElement.getBounds().getX(), coreUiElement.getBounds().getY(), coreUiElement.getBounds().getWidth(), coreUiElement.getBounds().getHeight(), new TextComponent(""));
        this.coreUiElement = coreUiElement;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        return this.coreUiElement.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
    {
        return this.coreUiElement.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers)
    {
        return this.coreUiElement.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        return coreUiElement.mouseClicked((int) pMouseX, (int) pMouseY, pButton);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick)
    {
        this.coreUiElement.render(ForgeRenderContext.of(pPoseStack), pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY)
    {
        this.coreUiElement.renderForeground(ForgeRenderContext.of(pPoseStack), pMouseX, pMouseY);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput)
    {
        // No-op for now
    }
}
