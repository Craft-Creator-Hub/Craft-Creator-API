package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Collapsible panel holding a scrollable column of labelled widgets (used for recipe settings).<br>
 * A toggle button sits on the panel's right edge when open and on its left edge when closed.
 */
public class CoreButtonWidgetList extends CoreUiElement
{
    private static final int PADDING = 5;
    private static final int ITEM_SPACING = 4;
    private static final int LABEL_GAP = 6;

    private final List<WidgetEntry> entries = new ArrayList<>();
    private final Identifier buttonTexture;
    private final CoreScrollbar scrollbar = new CoreScrollbar();
    private final Core2DBounds buttonBounds;

    @Getter
    private boolean isOpen = false;

    public CoreButtonWidgetList(String id, int x, int y, int width, int height, Core2DBounds buttonBounds, Identifier buttonTexture, String tooltip)
    {
        super(CoreUiElementType.WIDGET_LIST, id, x, y, width, height, tooltip);
        this.buttonTexture = buttonTexture;
        this.buttonBounds = buttonBounds;
        setOpen(false);
    }

    public void addWidget(String label, CoreUiElement widget)
    {
        entries.add(new WidgetEntry(label, widget));
        layout();
    }

    public void clear()
    {
        entries.clear();
        scrollbar.setScrollOffset(0);
        layout();
    }

    public void setOpen(boolean open)
    {
        isOpen = open;
        buttonBounds.setX(getBounds().getX() + (isOpen ? 0 : getBounds().getWidth()) - buttonBounds.getWidth());
        buttonBounds.setY(getBounds().getY());
    }

    /* ---------------- layout ---------------- */

    private int getContentX()
    {
        return getBounds().getX(PADDING);
    }

    private int getContentY()
    {
        return getBounds().getY(PADDING);
    }

    private int getContentWidth()
    {
        return Math.max(0, getBounds().getWidth() - PADDING * 2 - CoreScrollbar.WIDTH - ITEM_SPACING);
    }

    private int getContentHeight()
    {
        return Math.max(0, getBounds().getHeight() - PADDING * 2);
    }

    /**
     * Place every widget at its current scrolled position, so clicks hit the right place even before the next render.
     */
    private void layout()
    {
        int contentWidth = getContentWidth();
        int totalHeight = 0;
        for(WidgetEntry entry : entries)
            totalHeight += entry.getHeight(contentWidth) + ITEM_SPACING;

        scrollbar.update(getBounds().getRight(-PADDING - CoreScrollbar.WIDTH), getContentY(), getContentHeight(), Math.max(0, totalHeight - ITEM_SPACING));

        int currentY = getContentY() - scrollbar.getScrollOffset();
        for(WidgetEntry entry : entries)
        {
            entry.place(getContentX(), currentY, contentWidth);
            currentY += entry.getHeight(contentWidth) + ITEM_SPACING;
        }
    }

    private boolean isOverContent(int mouseX, int mouseY)
    {
        return mouseX >= getContentX() && mouseX < getContentX() + getContentWidth()
                && mouseY >= getContentY() && mouseY < getContentY() + getContentHeight();
    }

    private boolean isEntryVisible(WidgetEntry entry)
    {
        Core2DBounds b = entry.widget.getBounds();
        return b.getBottom() > getContentY() && b.getY() < getContentY() + getContentHeight();
    }

    @Override
    public boolean isMouseOverElement(int mouseX, int mouseY)
    {
        if(!isVisible()) return false;
        return buttonBounds.contains(mouseX, mouseY) || (isOpen && getBounds().contains(mouseX, mouseY));
    }

    /* ---------------- input ---------------- */

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(!isVisible()) return false;

        if(button == 0 && buttonBounds.contains(mouseX, mouseY))
        {
            setOpen(!isOpen);
            getUiAdapter().playSound(UiAdapter.UiSound.BUTTON_CLICK);
            return true;
        }

        if(!isOpen) return false;

        layout();

        // Let every widget see the click so text inputs can lose focus, but only visible ones can take it
        boolean handled = false;
        for(WidgetEntry entry : entries)
        {
            boolean canReceive = isOverContent(mouseX, mouseY) && isEntryVisible(entry);
            if(entry.widget.mouseClicked(canReceive ? mouseX : Integer.MIN_VALUE, canReceive ? mouseY : Integer.MIN_VALUE, button))
                handled = true;
        }
        if(handled) return true;

        if(scrollbar.mouseClicked(mouseX, mouseY)) return true;

        // Swallow clicks on the panel background so they don't reach what's behind it
        return getBounds().contains(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int button)
    {
        if(!scrollbar.mouseDragged(mouseY)) return false;
        layout();
        return true;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        return scrollbar.mouseReleased();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if(!isVisible() || !isOpen || !getBounds().contains((int) mouseX, (int) mouseY) || delta == 0) return false;

        for(WidgetEntry entry : entries)
            if(entry.widget.isMouseOverElement((int) mouseX, (int) mouseY) && entry.widget.mouseScrolled(mouseX, mouseY, delta))
                return true;

        scrollbar.scrollBy(delta > 0 ? -20 : 20);
        layout();
        return true;
    }

    @Override
    public void loseFocus()
    {
        entries.forEach(entry -> entry.widget.loseFocus());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(!isVisible() || !isOpen) return false;

        for(WidgetEntry entry : entries)
            if(entry.widget.keyPressed(keyCode, scanCode, modifiers))
                return true;
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if(!isVisible() || !isOpen) return false;

        for(WidgetEntry entry : entries)
            if(entry.widget.charTyped(codePoint, modifiers))
                return true;
        return false;
    }

    /* ---------------- rendering ---------------- */

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        if(ctx == null || !isVisible()) return;

        CoreScreenDefinition.renderTextureWithBounds(ctx, CoreScreenDefinition.GUI_TEXTURE, buttonBounds, buttonBounds.contains(mouseX, mouseY), false);
        getRenderAdapter().drawTexture(ctx, buttonTexture, buttonBounds.getX(4), buttonBounds.getY(4), buttonBounds.getWidth(-8), buttonBounds.getHeight(-8), 24, 24, 0, 0, 24, 24);

        if(!isOpen) return;

        CoreScreenDefinition.renderTextureWithBounds(ctx, CoreScreenDefinition.GUI_TEXTURE, getBounds(), false, false);

        layout();
        getRenderAdapter().enableScissor(ctx, getContentX(), getContentY(), getContentWidth(), getContentHeight());
        for(WidgetEntry entry : entries)
            if(isEntryVisible(entry))
                entry.render(ctx, getContentWidth(), mouseX, mouseY, partialTick);
        getRenderAdapter().disableScissor(ctx);

        scrollbar.render(ctx, getRenderAdapter(), mouseX, mouseY);
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        if(!isVisible()) return;

        if(!isOpen)
        {
            if(buttonBounds.contains(mouseX, mouseY))
                renderTooltip(ctx, mouseX, mouseY);
            return;
        }

        if(!isOverContent(mouseX, mouseY)) return;

        for(WidgetEntry entry : entries)
            if(isEntryVisible(entry))
                entry.widget.renderForeground(ctx, mouseX, mouseY);
    }

    private static class WidgetEntry
    {
        private final String label;
        private final CoreUiElement widget;

        private WidgetEntry(String label, CoreUiElement widget)
        {
            this.label = label == null ? "" : label;
            this.widget = widget;
        }

        private int getLabelWidth()
        {
            return label.isEmpty() ? 0 : getRenderAdapter().getTextWidth(label);
        }

        /**
         * The label goes above the widget when both don't fit on one line.
         */
        private boolean isLabelAbove(int availableWidth)
        {
            return !label.isEmpty() && getLabelWidth() + LABEL_GAP + widget.getBounds().getWidth() > availableWidth;
        }

        private int getHeight(int availableWidth)
        {
            if(isLabelAbove(availableWidth))
                return getRenderAdapter().getFontHeight() + 3 + widget.getBounds().getHeight();
            return Math.max(getRenderAdapter().getFontHeight(), widget.getBounds().getHeight());
        }

        private void place(int x, int y, int availableWidth)
        {
            if(isLabelAbove(availableWidth))
            {
                widget.getBounds().setX(x);
                widget.getBounds().setY(y + getRenderAdapter().getFontHeight() + 3);
            }
            else
            {
                widget.getBounds().setX(x + (label.isEmpty() ? 0 : getLabelWidth() + LABEL_GAP));
                widget.getBounds().setY(y);
            }
        }

        private void render(RenderContext ctx, int availableWidth, int mouseX, int mouseY, float partialTick)
        {
            if(!label.isEmpty())
            {
                int labelY = isLabelAbove(availableWidth)
                        ? widget.getBounds().getY() - getRenderAdapter().getFontHeight() - 3
                        : widget.getBounds().getVerticalCenter(getRenderAdapter().getFontHeight());
                int labelX = isLabelAbove(availableWidth) ? widget.getBounds().getX() : widget.getBounds().getX() - getLabelWidth() - LABEL_GAP;
                getRenderAdapter().drawText(ctx, label, labelX, labelY, 0xFFFFFFFF);
            }

            widget.render(ctx, mouseX, mouseY, partialTick);
        }
    }
}
