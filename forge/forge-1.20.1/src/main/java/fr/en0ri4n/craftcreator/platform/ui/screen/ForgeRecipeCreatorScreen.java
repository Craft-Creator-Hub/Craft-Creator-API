package fr.en0ri4n.craftcreator.platform.ui.screen;

import net.minecraft.client.gui.GuiGraphics;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.screen.WidgetRenderer;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeWidget;
import lombok.Getter;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Getter
public class ForgeRecipeCreatorScreen extends AbstractContainerScreen<ForgeRecipeCreatorMenu> implements WidgetRenderer
{
    @lombok.Getter
    private final ContainerModel<?> model;

    public ForgeRecipeCreatorScreen(ForgeRecipeCreatorMenu menu, Inventory playerInv, Component title)
    {
        super(menu, playerInv, Component.empty());
        this.model = menu.getModel();
        this.imageWidth = model.getLayout().getWidth();
        this.imageHeight = model.getLayout().getHeight();
    }

    @Override
    protected void init()
    {
        super.init();

        model.getScreenDefinition().init(this, this.width, this.height);
    }

    @Override
    public void addWidgetToScreen(Object widget)
    {
        this.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) widget);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        ForgeWidget.dispatchFocusLoss(children(), pMouseX, pMouseY);
        boolean impl = this.model.getScreenDefinition().onClick((pMouseX - this.leftPos), (pMouseY - this.topPos), pButton);
        return impl || super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    /**
     * A focused text input gets keys first, so typing "e" or a number doesn't close the screen or move items.
     */
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        if(pKeyCode != 256 && this.getFocused() != null && this.getFocused().keyPressed(pKeyCode, pScanCode, pModifiers))
            return true;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
    {
        // AbstractContainerScreen only handles slot dragging, forward to the focused widget first (e.g. a list scrollbar)
        if(this.isDragging() && this.getFocused() != null && this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY))
            return true;
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void onClose()
    {
        model.getScreenDefinition().onClose();
        super.onClose();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {}

    @Override
    protected void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        ForgeRenderContext ctx = ForgeRenderContext.of(graphics);
        model.getScreenDefinition().renderBackground(ctx);
        super.render(graphics, mouseX, mouseY, partialTicks);
        model.getScreenDefinition().render(ctx, mouseX, mouseY);
        model.getScreenDefinition().renderForeground(ctx, mouseX, mouseY);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
