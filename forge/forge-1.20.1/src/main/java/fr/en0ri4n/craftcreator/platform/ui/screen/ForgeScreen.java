package fr.en0ri4n.craftcreator.platform.ui.screen;

import net.minecraft.client.gui.GuiGraphics;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.WidgetRenderer;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeWidget;
import lombok.Getter;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Getter
public class ForgeScreen extends Screen implements WidgetRenderer
{
    private final CoreScreenDefinition<?> screenDefinition;

    public ForgeScreen(CoreScreenDefinition<?> screenDefinition)
    {
        super(Component.literal(screenDefinition.getTitle()));
        this.screenDefinition = screenDefinition;
    }

    @Override
    protected void init()
    {
        super.init();

        screenDefinition.init(this, this.width, this.height);
    }

    /**
     * Screens talk to the server (e.g. recipe management), which must keep running in singleplayer.
     */
    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void addWidgetToScreen(Object widget)
    {
        this.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) widget);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        ForgeWidget.dispatchFocusLoss(children(), mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics graphics)
    {
        screenDefinition.renderBackground(ForgeRenderContext.of(graphics));
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick)
    {
        ForgeRenderContext ctx = ForgeRenderContext.of(graphics);
        screenDefinition.renderBackground(ctx);
        screenDefinition.render(ctx, pMouseX, pMouseY);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        screenDefinition.renderForeground(ctx, pMouseX, pMouseY);
    }
}
