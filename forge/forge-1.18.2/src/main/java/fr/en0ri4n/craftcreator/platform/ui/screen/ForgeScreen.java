package fr.en0ri4n.craftcreator.platform.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.WidgetRenderer;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeUiAdapter;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class ForgeScreen extends Screen implements WidgetRenderer
{
    private final CoreScreenDefinition<?> screenDefinition;

    public ForgeScreen(CoreScreenDefinition<?> screenDefinition)
    {
        super(new TextComponent(screenDefinition.getTitle()));
        this.screenDefinition = screenDefinition;
    }

    @Override
    protected void init()
    {
        super.init();

        screenDefinition.init(this);
    }

    @Override
    public void addWidgetToScreen(Object widget)
    {
        this.addRenderableWidget((GuiEventListener & Widget & NarratableEntry) widget);
    }

    @Override
    public void renderBackground(PoseStack pPoseStack)
    {
        renderBackground(pPoseStack, 0);
        screenDefinition.renderBackground(ForgeRenderContext.of(pPoseStack, 0.0F));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick)
    {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        screenDefinition.render(ForgeRenderContext.of(pPoseStack, pPartialTick));
    }
}
