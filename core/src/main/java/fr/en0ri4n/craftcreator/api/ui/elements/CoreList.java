package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class CoreList<T> extends CoreUiElement
{
    public static final int DEFAULT_ITEM_HEIGHT = 20;

    @Getter
    private final List<Entry<T>> entries = new ArrayList<>();
    private int x, y, width, height;
    @Getter
    private int itemHeight = DEFAULT_ITEM_HEIGHT;

    @Getter
    private int scrollOffset = 0;

    @Getter
    private int selectedIndex = -1;
    private int hoveredIndex = -1;

    private Consumer<Optional<Entry<T>>> selectionListener = e ->
    {};

    public CoreList(int x, int y, int width, int height, int itemHeight, List<Entry<T>> initialEntries)
    {
        super(CoreUiElementType.LIST, x, y, width, height, "");
        setEntries(initialEntries);
        setBounds(x, y, width, height);
        this.itemHeight = itemHeight;
    }

    public static <T> CoreList<T> countedList(int x, int y, int width, int displayedEntriesCount, int itemHeight, List<Entry<T>> initialEntries)
    {
        CoreList<T> coreList = new CoreList<>(x, y, width, 1, itemHeight, initialEntries);
        coreList.setBounds(x, y, width, displayedEntriesCount * itemHeight + coreList.getOffset() * 2);
        return coreList;
    }

    public void setBounds(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setEntries(List<Entry<T>> newEntries)
    {
        this.entries.clear();
        if(newEntries != null) this.entries.addAll(newEntries);
        clampScroll();
        if(selectedIndex >= entries.size()) setSelectedIndex(-1);
    }

    public void setItemHeight(int itemHeight)
    {
        this.itemHeight = Math.max(4, itemHeight);
    }

    public void addEntry(Entry<T> e)
    {
        entries.add(Objects.requireNonNull(e));
        clampScroll();
    }

    public void clear()
    {
        entries.clear();
        selectedIndex = -1;
        hoveredIndex = -1;
        scrollOffset = 0;
    }

    public Optional<Entry<T>> getSelected()
    {
        if(selectedIndex < 0 || selectedIndex >= entries.size()) return Optional.empty();
        return Optional.of(entries.get(selectedIndex));
    }

    public void setSelectionListener(Consumer<Optional<Entry<T>>> listener)
    {
        this.selectionListener = listener == null ? e ->
        {} : listener;
    }

    public void setSelectedIndex(int idx)
    {
        if(idx < 0 || idx >= entries.size())
        {
            this.selectedIndex = -1;
        }
        else
        {
            this.selectedIndex = idx;
            ensureIndexVisible(idx);
        }
        selectionListener.accept(getSelected());
    }

    public void scrollByItems(int deltaItems)
    {
        scrollOffset += deltaItems * itemHeight;
        clampScroll();
    }

    private int getOffset()
    {
        return 4;
    }

    private int getScrollBarWidth()
    {
        return 6;
    }

    private int getContentTop()
    {
        return y + getOffset();
    }

    private int getContentHeight()
    {
        return Math.max(0, height - getOffset() * 2);
    }

    private void clampScroll()
    {
        int max = Math.max(0, entries.size() * itemHeight - getContentHeight());
        if(scrollOffset < 0) scrollOffset = 0;
        if(scrollOffset > max) scrollOffset = max;
    }

    private void ensureIndexVisible(int idx)
    {
        if(idx < 0 || idx >= entries.size()) return;
        int topOfIdx = idx * itemHeight;
        int bottomOfIdx = topOfIdx + itemHeight;
        int ch = getContentHeight();
        if(topOfIdx < scrollOffset)
        {
            scrollOffset = topOfIdx;
        }
        else if(bottomOfIdx > scrollOffset + ch)
        {
            scrollOffset = bottomOfIdx - ch;
        }
        clampScroll();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        int steps = 0;
        if(delta > 0) steps = -1;
        else if(delta < 0) steps = 1;
        if(steps != 0) scrollByItems(steps);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(keyCode == 265) // up
        {
            scrollByItems(-1);
            return true;
        }
        else if(keyCode == 264) // down
        {
            scrollByItems(1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float pPartialTick)
    {
        if(ctx == null) return;

        getRenderAdapter().drawRect(ctx, getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds().getHeight(), 0xFF1F2933);
        getRenderAdapter().drawRect(ctx, x + getOffset(), y + getOffset(), width - getOffset() * 2, height - getOffset() * 2, 0xFF0F1416);

        if(entries.isEmpty() || getContentHeight() <= 0) return;

        int firstIndex = scrollOffset / itemHeight;
        int lastIndex = Math.min(entries.size() - 1, (scrollOffset + getContentHeight() - 1) / itemHeight);

        hoveredIndex = -1;
        int contentLeft = x + getOffset();
        int contentRight = x + width - getOffset() - getScrollBarWidth();
        int contentTop = getContentTop();
        int contentBottom = contentTop + getContentHeight();

        if(mouseX >= contentLeft && mouseX < contentRight && mouseY >= contentTop && mouseY < contentBottom)
        {
            int relY = mouseY - contentTop + scrollOffset;
            int idx = relY / itemHeight;
            if(idx >= 0 && idx < entries.size()) hoveredIndex = idx;
        }

        for(int i = firstIndex; i <= lastIndex; i++)
        {
            Entry<T> e = entries.get(i);
            int entryTop = contentTop + (i * itemHeight - scrollOffset);
            boolean hovered = (i == hoveredIndex);
            boolean selected = (i == selectedIndex);
            e.render(ctx, i, contentLeft, entryTop, width - getScrollBarWidth() - getOffset() * 2, itemHeight, mouseX, mouseY, selected, hovered);
        }

        drawScrollBar(ctx);
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        if(hoveredIndex >= 0 && hoveredIndex < entries.size())
        {
            Entry<T> hoveredEntry = entries.get(hoveredIndex);
            hoveredEntry.renderForeground(ctx, hoveredIndex, x + getOffset(), getContentTop() + (hoveredIndex * itemHeight - scrollOffset), width - getScrollBarWidth() - getOffset() * 2, itemHeight, mouseX, mouseY, false, true);
        }
    }

    private void drawScrollBar(RenderContext ctx)
    {
        int contentHeight = entries.size() * itemHeight;
        int ch = getContentHeight();
        if(contentHeight <= ch) return;

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

    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        int contentLeft = x + getOffset();
        int contentRight = x + width - getOffset() - getScrollBarWidth();
        int contentTop = getContentTop();
        int contentBottom = contentTop + getContentHeight();

        // Click on content area (select)
        if(mouseX >= contentLeft && mouseX < contentRight && mouseY >= contentTop && mouseY < contentBottom)
        {
            int relY = mouseY - contentTop + scrollOffset;
            int idx = relY / itemHeight;
            if(idx < 0 || idx >= entries.size()) return false;

            setSelectedIndex(idx);
            Entry<T> e = entries.get(idx);
            if(e.onClick != null)
            {
                e.onClick.accept(e);
                return true;
            }
            return true;
        }

        // Click on scrollbar area
        int barLeft = x + width - getScrollBarWidth() - getOffset();
        int barRight = x + width - getOffset();
        if(mouseX >= barLeft && mouseX < barRight && mouseY >= contentTop && mouseY < contentBottom)
        {
            int contentHeight = entries.size() * itemHeight;
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

    @Getter
    public static class Entry<T>
    {
        private final String label;
        private final T value;
        private final Identifier iconId;
        private final Consumer<Entry<T>> onClick;

        public Entry(String label, T value, Identifier iconId, Consumer<Entry<T>> onClick)
        {
            this.label = Objects.requireNonNull(label);
            this.value = value;
            this.iconId = iconId;
            this.onClick = onClick;
        }

        public Entry(String label, T value, Consumer<Entry<T>> onClick)
        {
            this(label, value, null, onClick);
        }

        public void render(RenderContext ctx, int index, int x, int entryTop, int width, int itemHeight, int mouseX, int mouseY, boolean selected, boolean hovered)
        {
            if(selected)
            {
                getRenderAdapter().drawRect(ctx, x + 6, entryTop + 2, width - 12, itemHeight - 4, 0xAA2B8AE6);
            }
            else if(hovered)
            {
                getRenderAdapter().drawRect(ctx, x + 6, entryTop + 2, width - 12, itemHeight - 4, 0x552B8AE6);
            }
            else if((index & 1) == 0)
            {
                getRenderAdapter().drawRect(ctx, x + 6, entryTop + 2, width - 12, itemHeight - 4, 0xFF0D1112);
            }

            float iconX = x + 10f;
            float iconY = entryTop + (itemHeight - 16) / 2f;
            if(iconId != null)
            {
                getRenderAdapter().drawItem(ctx, new CoreItemStack(iconId, 1), (int) iconX, (int) iconY, 1F);
            }

            float textX = x + 10f + (iconId != null ? 20f : 0f);
            float textY = entryTop + (itemHeight - 8) / 2f;
            int color = selected ? 0xFFFFFFFF : (hovered ? 0xFFEFEFEF : 0xFFDDE6EE);
            getRenderAdapter().drawText(ctx, label, (int) textX, (int) textY, color);
        }

        public void renderForeground(RenderContext ctx, int index, int x, int y, int width, int itemHeight, int mouseX, int mouseY, boolean selected, boolean hovered)
        {
            if(!hovered) return;

            getRenderAdapter().drawRect(ctx, mouseX + 12, mouseY + 12, 100, 40, 0xCC000000);
            getRenderAdapter().drawText(ctx, "Entry: " + label, mouseX + 16, mouseY + 16, 0xFFFFFFFF);
        }

        protected RenderAdapter getRenderAdapter()
        {
            return CraftCreatorAPI.get().getPlatform().getRenderAdapter();
        }
    }
}