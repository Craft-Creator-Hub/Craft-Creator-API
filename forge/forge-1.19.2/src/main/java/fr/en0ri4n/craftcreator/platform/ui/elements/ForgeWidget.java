package fr.en0ri4n.craftcreator.platform.ui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ForgeWidget extends AbstractWidget implements Widget
{
    private final CoreUiElement coreUiElement;

    public ForgeWidget(CoreUiElement coreUiElement)
    {
        super(coreUiElement.getBounds().getX(), coreUiElement.getBounds().getY(), coreUiElement.getBounds().getWidth(), coreUiElement.getBounds().getHeight(), Component.literal(""));
        this.coreUiElement = coreUiElement;
    }

    public CoreUiElement getCoreElement()
    {
        return coreUiElement;
    }

    /**
     * Tell every core widget of the screen except the clicked ones that they lost focus.
     */
    public static void dispatchFocusLoss(List<? extends GuiEventListener> children, double mouseX, double mouseY)
    {
        for(var child : children)
            if(child instanceof ForgeWidget widget && !widget.isMouseOver(mouseX, mouseY))
                widget.getCoreElement().loseFocus();
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
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
    {
        return coreUiElement.mouseDragged((int) pMouseX, (int) pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton)
    {
        return coreUiElement.mouseReleased((int) pMouseX, (int) pMouseY, pButton);
    }

    /**
     * Core elements can move or resize after creation, so always ask them instead of using the widget's own fields.
     */
    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY)
    {
        return coreUiElement.isMouseOverElement((int) pMouseX, (int) pMouseY);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick)
    {
        this.x = coreUiElement.getBounds().getX();
        this.y = coreUiElement.getBounds().getY();
        this.width = coreUiElement.getBounds().getWidth();
        this.height = coreUiElement.getBounds().getHeight();
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
