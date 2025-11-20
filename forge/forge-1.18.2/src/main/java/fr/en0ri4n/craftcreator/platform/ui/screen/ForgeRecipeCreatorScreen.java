package fr.en0ri4n.craftcreator.platform.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.screen.WidgetRenderer;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ForgeRecipeCreatorScreen extends AbstractContainerScreen<ForgeRecipeCreatorMenu> implements WidgetRenderer
{
    private final ContainerModel<?> model;

    public ForgeRecipeCreatorScreen(ForgeRecipeCreatorMenu menu, Inventory playerInv, Component title)
    {
        super(menu, playerInv, title);
        this.model = menu.getModel();
        this.imageWidth = model.getLayout().getWidth();
        this.imageHeight = model.getLayout().getHeight();
    }

    public ContainerModel<?> getModel()
    {
        return model;
    }

    @Override
    protected void init()
    {
        super.init();

        model.getScreenDefinition().init(this);
    }

    @Override
    public void addWidgetToScreen(Object widget)
    {
        this.addRenderableWidget((GuiEventListener & Widget & NarratableEntry) widget);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        boolean impl = this.model.getScreenDefinition().onClick((pMouseX - this.leftPos), (pMouseY - this.topPos), pButton);
        return impl || super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void onClose()
    {
        model.getScreenDefinition().onClose();
        super.onClose();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        ForgeRenderContext ctx = new ForgeRenderContext(poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), partialTicks);
        model.getScreenDefinition().renderBackground(ctx);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }
}