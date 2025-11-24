package fr.en0ri4n.craftcreator.platform.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.screen.WidgetRenderer;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import lombok.Getter;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

@Getter
public class ForgeRecipeCreatorScreen extends AbstractContainerScreen<ForgeRecipeCreatorMenu> implements WidgetRenderer
{
    private final ContainerModel<?> model;

    public ForgeRecipeCreatorScreen(ForgeRecipeCreatorMenu menu, Inventory playerInv, Component title)
    {
        super(menu, playerInv, new TextComponent(""));
        this.model = menu.getModel();
        this.imageWidth = model.getLayout().getWidth();
        this.imageHeight = model.getLayout().getHeight();
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
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {}

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {}

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        model.getScreenDefinition().renderBackground(ForgeRenderContext.of(poseStack));
        super.render(poseStack, mouseX, mouseY, partialTicks);
        model.getScreenDefinition().render(ForgeRenderContext.of(poseStack), mouseX, mouseY);
        model.getScreenDefinition().renderForeground(ForgeRenderContext.of(poseStack), mouseX, mouseY);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }
}