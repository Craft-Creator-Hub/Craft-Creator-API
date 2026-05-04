package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CoreButtonWidgetList extends CoreUiElement
{
    private final List<WidgetEntry> entries = new ArrayList<>();
    private final Identifier buttonTexture;
    private int scrollOffset = 0;
    private final int itemSpacing = 4;
    
    @Getter
    @Setter
    private boolean isOpen = false;
    private final Core2DBounds buttonBounds;

    public CoreButtonWidgetList(String id, int x, int y, int width, int height, Core2DBounds buttonBounds, Identifier buttonTexture, String tooltip)
    {
        super(CoreUiElementType.WIDGET_LIST, id, x, y, width, height, tooltip);
        this.buttonTexture = buttonTexture;
        buttonBounds.setX(x + width - 20);
        buttonBounds.setY(y);
        this.buttonBounds = buttonBounds;
    }

    public void addWidget(String label, CoreUiElement widget)
    {
        entries.add(new WidgetEntry(label, widget));
        clampScroll();
    }

    public void clear()
    {
        entries.clear();
        scrollOffset = 0;
    }

    private int getOffset()
    {
        return 5;
    }

    private int getScrollBarWidth()
    {
        return 6;
    }

    private int getContentTop()
    {
        return getBounds().getY() + getOffset();
    }

    private int getContentHeight()
    {
        return Math.max(0, getBounds().getHeight() - getOffset() * 2);
    }

    private int getTotalContentHeight()
    {
        int totalHeight = 0;
        int listWidth = getBounds().getWidth() - getOffset() * 2 - getScrollBarWidth() - itemSpacing;

        for(WidgetEntry entry : entries)
        {
            totalHeight += entry.calculateHeight(listWidth) + itemSpacing;
        }

        return Math.max(0, totalHeight - itemSpacing);
    }

    private void clampScroll()
    {
        int max = Math.max(0, getTotalContentHeight() - getContentHeight());
        if(scrollOffset < 0) scrollOffset = 0;
        if(scrollOffset > max) scrollOffset = max;
    }

    public void scrollByPixels(int deltaPixels)
    {
        scrollOffset += deltaPixels;
        clampScroll();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if(!isVisible() || !isOpen()) return false;

        int steps = 0;
        if(delta > 0) steps = -20;
        else if(delta < 0) steps = 20;

        if(steps != 0)
        {
            scrollByPixels(steps);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(!isVisible() || !isOpen()) return false;

        for(WidgetEntry entry : entries)
        {
            if(entry.widget.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if(!isVisible() || !isOpen()) return false;

        for(WidgetEntry entry : entries)
        {
            if(entry.widget.charTyped(codePoint, modifiers))
                return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    public void setOpen(boolean open)
    {
        isOpen = open;
        buttonBounds.setX(getBounds().getX() + (!isOpen ? getBounds().getWidth() : 0) - buttonBounds.getWidth());
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        if(ctx == null || !isVisible()) return;

        // Render the button background
        CoreScreenDefinition.renderTextureWithSize(ctx, CoreScreenDefinition.GUI_TEXTURE, buttonBounds.getX(), buttonBounds.getY(), buttonBounds.getWidth(), buttonBounds.getHeight(), false, false);
        getRenderAdapter().drawTexture(ctx,
                buttonTexture, 
                buttonBounds.getX() + 4, 
                buttonBounds.getY() + 4,
                12,
                12, 
                24,
                24,
                0,
                0,
                24,
                24);
        
        if(!isOpen()) return;

        int x = getBounds().getX();
        int y = getBounds().getY();
        int width = getBounds().getWidth();
        int height = getBounds().getHeight();

        CoreScreenDefinition.renderTextureWithSize(ctx, CoreScreenDefinition.GUI_TEXTURE, x, y, width, height, false, false);

        if(entries.isEmpty() || getContentHeight() <= 0) return;

        int contentLeft = x + getOffset();
        int contentTop = getContentTop();
        int contentWidth = width - getOffset() * 2 - getScrollBarWidth() - itemSpacing;

        int currentY = contentTop - scrollOffset;

        // Scissor support would be ideal here depending on RenderAdapter capabilities
        for(WidgetEntry entry : entries)
        {
            int entryHeight = entry.calculateHeight(contentWidth);
            
            // Only render if visible (or partially visible)
            if(currentY + entryHeight > contentTop && currentY < contentTop + getContentHeight())
            {
                entry.render(ctx, contentLeft, currentY, contentWidth, mouseX, mouseY, partialTick);
            }
            
            currentY += entryHeight + itemSpacing;
        }

        drawScrollBar(ctx);
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        if(!isVisible()) return;
        
        if(!isOpen())
        {
            getRenderAdapter().drawRect(ctx, getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds().getHeight(), 0x55FFFFFF);
            return;
        }

        int contentTop = getContentTop();
        int contentLeft = getBounds().getX() + getOffset();
        int contentWidth = getBounds().getWidth() - getOffset() * 2 - getScrollBarWidth() - itemSpacing;
        int currentY = contentTop - scrollOffset;

        for(WidgetEntry entry : entries)
        {
            int entryHeight = entry.calculateHeight(contentWidth);
            
            if(currentY + entryHeight > contentTop && currentY < contentTop + getContentHeight())
            {
                entry.widget.renderForeground(ctx, mouseX, mouseY);
            }
            
            currentY += entryHeight + itemSpacing;
        }
    }

    private void drawScrollBar(RenderContext ctx)
    {
        int contentHeight = getTotalContentHeight();
        int ch = getContentHeight();
        if(contentHeight <= ch) return;

        int x = getBounds().getX();
        int width = getBounds().getWidth();

        int barX = x + width - getScrollBarWidth() - getOffset();
        int barY = getContentTop();
        int barW = getScrollBarWidth();
        int barH = ch;

        getRenderAdapter().drawRect(ctx, barX, barY, barW, barH, 0x66191919);

        float ratio = (float) ch / (float) contentHeight;
        int thumbH = Math.max(8, (int) (barH * ratio));
        int maxThumbY = barH - thumbH;
        int thumbY = maxThumbY == 0 ? 0 : (int) ((float) scrollOffset / Math.max(1, contentHeight - ch) * maxThumbY);

        getRenderAdapter().drawRect(ctx, barX, barY + thumbY, barW, thumbH, 0xAA2B8AE6);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(!isVisible()) return false;
        
        if(button == 0 && buttonBounds.contains(mouseX, mouseY))
        {
            setOpen(!isOpen);            
            return true;
        }

        int x = getBounds().getX();
        int width = getBounds().getWidth();
        int contentLeft = x + getOffset();
        int contentTop = getContentTop();
        int contentBottom = contentTop + getContentHeight();
        int contentWidth = width - getOffset() * 2 - getScrollBarWidth() - itemSpacing;

        // Click on content area
        int contentRight = x + width - getOffset() - getScrollBarWidth();
        if(mouseX >= contentLeft && mouseX < contentRight && mouseY >= contentTop && mouseY < contentBottom)
        {
            int currentY = contentTop - scrollOffset;
            for(WidgetEntry entry : entries)
            {
                int entryHeight = entry.calculateHeight(contentWidth);
                if(currentY + entryHeight > contentTop && currentY < contentBottom)
                {
                    if (entry.widget.mouseClicked(mouseX, mouseY, button))
                    {
                        return true;
                    }
                }
                currentY += entryHeight + itemSpacing;
            }
            return true;
        }

        // Click on scrollbar area
        int barLeft = x + width - getScrollBarWidth() - getOffset();
        int barRight = x + width - getOffset();
        if(mouseX >= barLeft && mouseX < barRight && mouseY >= contentTop && mouseY < contentBottom)
        {
            int contentHeight = getTotalContentHeight();
            if(contentHeight <= getContentHeight()) return false;

            int barY = contentTop;
            int barH = getContentHeight();

            float ratio = (float) getContentHeight() / (float) contentHeight;
            int thumbH = Math.max(8, (int) (barH * ratio));
            int maxThumbY = barH - thumbH;

            int clickY = mouseY - barY - thumbH / 2;
            if(clickY < 0) clickY = 0;
            if(clickY > maxThumbY) clickY = maxThumbY;

            scrollOffset = maxThumbY == 0 ? 0 : (int) ((float) clickY / (float) maxThumbY * (contentHeight - getContentHeight()));
            clampScroll();
            return true;
        }

        return false;
    }

    private static class WidgetEntry
    {
        private final String label;
        private final CoreUiElement widget;

        public WidgetEntry(String label, CoreUiElement widget)
        {
            this.label = label;
            this.widget = widget;
        }

        public int calculateHeight(int availableWidth)
        {
            int labelWidth = label != null && !label.isEmpty() ? getRenderAdapter().getTextWidth(label) : 0;
            boolean labelAbove = label != null && !label.isEmpty() && (labelWidth + 10 + widget.getBounds().getWidth() > availableWidth);

            if(labelAbove)
            {
                return 12 + widget.getBounds().getHeight(); // Label height (e.g. 10) + margin + widget
            }
            else
            {
                return Math.max(10, widget.getBounds().getHeight()); // Max of label height and widget height
            }
        }

        public void render(RenderContext ctx, int x, int y, int availableWidth, int mouseX, int mouseY, float partialTick)
        {
            int labelWidth = label != null && !label.isEmpty() ? getRenderAdapter().getTextWidth(label) : 0;
            boolean labelAbove = label != null && !label.isEmpty() && (labelWidth + 10 + widget.getBounds().getWidth() > availableWidth);

            if(labelAbove)
            {
                getRenderAdapter().drawText(ctx, label, x, y, 0xFFFFFFFF);
                
                // Update widget position
                widget.getBounds().setX(x);
                widget.getBounds().setY(y + 12);
            }
            else
            {
                int widgetX = x;
                if (label != null && !label.isEmpty())
                {
                    getRenderAdapter().drawText(ctx, label, x, y + (widget.getBounds().getHeight() - 8) / 2, 0xFFFFFFFF);
                    widgetX += labelWidth + 10;
                }
                
                // Update widget position
                widget.getBounds().setX(widgetX);
                widget.getBounds().setY(y);
            }

            widget.render(ctx, mouseX, mouseY, partialTick);
        }
    }
}
