package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Scrollable, selectable list of entries with an optional filter.<br>
 * Everything is computed from {@link #getBounds()}, so the list can be moved or resized at any time.
 */
public class CoreList<T> extends CoreUiElement
{
    public static final int DEFAULT_ITEM_HEIGHT = 20;
    private static final int PADDING = 4;
    private static final int KEY_UP = 265;
    private static final int KEY_DOWN = 264;

    private final List<Entry<T>> entries = new ArrayList<>();
    /** Entries matching the current filter, in display order. */
    private final List<Entry<T>> visibleEntries = new ArrayList<>();
    private final CoreScrollbar scrollbar = new CoreScrollbar();

    @Getter
    private int itemHeight;
    private Predicate<Entry<T>> filter = entry -> true;
    private Entry<T> selected;
    private Entry<T> hovered;
    @Setter
    private String emptyText = "";

    private Consumer<Optional<Entry<T>>> selectionListener = e -> {};

    public CoreList(int x, int y, int width, int height, int itemHeight, List<Entry<T>> initialEntries)
    {
        super(CoreUiElementType.LIST, x, y, width, height, "");
        this.itemHeight = Math.max(4, itemHeight);
        setEntries(initialEntries);
    }

    /**
     * Create a list tall enough to display exactly {@code displayedEntriesCount} entries.
     */
    public static <T> CoreList<T> countedList(int x, int y, int width, int displayedEntriesCount, int itemHeight, List<Entry<T>> initialEntries)
    {
        return new CoreList<>(x, y, width, displayedEntriesCount * itemHeight + PADDING * 2, itemHeight, initialEntries);
    }

    /* ---------------- entries & filter ---------------- */

    public List<Entry<T>> getEntries()
    {
        return Collections.unmodifiableList(entries);
    }

    public List<Entry<T>> getVisibleEntries()
    {
        return Collections.unmodifiableList(visibleEntries);
    }

    public void setEntries(List<Entry<T>> newEntries)
    {
        entries.clear();
        if(newEntries != null) entries.addAll(newEntries);
        refresh();
    }

    public void addEntry(Entry<T> entry)
    {
        entries.add(Objects.requireNonNull(entry));
        refresh();
    }

    public void clear()
    {
        entries.clear();
        scrollbar.setScrollOffset(0);
        refresh();
    }

    /**
     * Only show entries matching the given predicate. Pass null to show everything.
     * The selection is kept if the selected entry is still visible.
     */
    public void setFilter(Predicate<Entry<T>> filter)
    {
        this.filter = filter == null ? entry -> true : filter;
        scrollbar.setScrollOffset(0);
        refresh();
    }

    /**
     * Convenience filter matching the label, case-insensitive.
     */
    public void filterByLabel(String search)
    {
        String lowerSearch = search == null ? "" : search.trim().toLowerCase();
        setFilter(lowerSearch.isEmpty() ? null : entry -> entry.getLabel().toLowerCase().contains(lowerSearch));
    }

    private void refresh()
    {
        visibleEntries.clear();
        entries.stream().filter(filter).forEach(visibleEntries::add);
        hovered = null;

        if(selected != null && !visibleEntries.contains(selected)) select(null);
        updateScrollbar();
    }

    public void setItemHeight(int itemHeight)
    {
        this.itemHeight = Math.max(4, itemHeight);
        updateScrollbar();
    }

    /* ---------------- selection ---------------- */

    public Optional<Entry<T>> getSelected()
    {
        return Optional.ofNullable(selected);
    }

    /**
     * @return the index of the selected entry among the visible entries, or -1
     */
    public int getSelectedIndex()
    {
        return selected == null ? -1 : visibleEntries.indexOf(selected);
    }

    public void setSelectionListener(Consumer<Optional<Entry<T>>> listener)
    {
        this.selectionListener = listener == null ? e -> {} : listener;
    }

    public void setSelectedIndex(int index)
    {
        select(index < 0 || index >= visibleEntries.size() ? null : visibleEntries.get(index));
    }

    public void setSelectedValue(T value)
    {
        select(visibleEntries.stream().filter(entry -> Objects.equals(entry.getValue(), value)).findFirst().orElse(null));
    }

    private void select(Entry<T> entry)
    {
        if(entry == selected) return;

        selected = entry;
        if(entry != null)
        {
            int top = visibleEntries.indexOf(entry) * itemHeight;
            scrollbar.ensureVisible(top, top + itemHeight);
        }
        selectionListener.accept(getSelected());
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
        return Math.max(0, getBounds().getWidth() - PADDING * 2 - (scrollbar.isNeeded() ? CoreScrollbar.WIDTH + 2 : 0));
    }

    private int getContentHeight()
    {
        return Math.max(0, getBounds().getHeight() - PADDING * 2);
    }

    private void updateScrollbar()
    {
        scrollbar.update(getBounds().getRight(-PADDING - CoreScrollbar.WIDTH), getContentY(), getContentHeight(), visibleEntries.size() * itemHeight);
    }

    private boolean isOverContent(int mouseX, int mouseY)
    {
        return mouseX >= getContentX() && mouseX < getContentX() + getContentWidth()
                && mouseY >= getContentY() && mouseY < getContentY() + getContentHeight();
    }

    private Entry<T> getEntryAt(int mouseX, int mouseY)
    {
        if(!isOverContent(mouseX, mouseY)) return null;
        int index = (mouseY - getContentY() + scrollbar.getScrollOffset()) / itemHeight;
        return index >= 0 && index < visibleEntries.size() ? visibleEntries.get(index) : null;
    }

    /* ---------------- input ---------------- */

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(!isMouseOver(mouseX, mouseY)) return false;

        updateScrollbar();
        if(scrollbar.mouseClicked(mouseX, mouseY)) return true;

        Entry<T> entry = getEntryAt(mouseX, mouseY);
        if(entry != null)
        {
            select(entry);
            if(entry.getOnClick() != null) entry.getOnClick().accept(entry);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int button)
    {
        return scrollbar.mouseDragged(mouseY);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        return scrollbar.mouseReleased();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if(!isMouseOver((int) mouseX, (int) mouseY) || delta == 0) return false;
        scrollbar.scrollBy(delta > 0 ? -itemHeight : itemHeight);
        return true;
    }

    /**
     * Up/down arrows move the selection. Only reached when the list has focus (after a click on it).
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(!isVisible() || visibleEntries.isEmpty()) return false;

        int index = getSelectedIndex();
        if(keyCode == KEY_UP)
        {
            setSelectedIndex(index <= 0 ? 0 : index - 1);
            return true;
        }
        if(keyCode == KEY_DOWN)
        {
            setSelectedIndex(Math.min(visibleEntries.size() - 1, index + 1));
            return true;
        }
        return false;
    }

    /* ---------------- rendering ---------------- */

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        if(ctx == null || !isVisible()) return;

        updateScrollbar();
        RenderAdapter adapter = getRenderAdapter();
        Core2DBounds b = getBounds();

        adapter.drawRect(ctx, b.getX(), b.getY(), b.getWidth(), b.getHeight(), 0xFF1F2933);
        adapter.drawRect(ctx, getContentX(), getContentY(), b.getWidth() - PADDING * 2, getContentHeight(), 0xFF0F1416);

        hovered = scrollbar.isDragging() ? null : getEntryAt(mouseX, mouseY);

        if(visibleEntries.isEmpty())
        {
            if(!emptyText.isEmpty())
                adapter.drawText(ctx, emptyText, b.getHorizontalCenter(adapter.getTextWidth(emptyText)), b.getVerticalCenter(adapter.getFontHeight()), 0xFF808080);
            return;
        }

        int scroll = scrollbar.getScrollOffset();
        int firstIndex = scroll / itemHeight;
        int lastIndex = Math.min(visibleEntries.size() - 1, (scroll + getContentHeight()) / itemHeight);

        adapter.enableScissor(ctx, getContentX(), getContentY(), getContentWidth(), getContentHeight());
        for(int i = firstIndex; i <= lastIndex; i++)
        {
            Entry<T> entry = visibleEntries.get(i);
            int entryTop = getContentY() + i * itemHeight - scroll;
            entry.render(ctx, i, getContentX(), entryTop, getContentWidth(), itemHeight, mouseX, mouseY, entry == selected, entry == hovered);
        }
        adapter.disableScissor(ctx);

        scrollbar.render(ctx, adapter, mouseX, mouseY);
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        if(hovered == null || !isVisible()) return;

        int index = visibleEntries.indexOf(hovered);
        if(index < 0) return;

        int entryTop = getContentY() + index * itemHeight - scrollbar.getScrollOffset();
        hovered.renderForeground(ctx, index, getContentX(), entryTop, getContentWidth(), itemHeight, mouseX, mouseY, hovered == selected, true);
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
            int background = selected ? 0xAA2B8AE6 : hovered ? 0x552B8AE6 : (index & 1) == 0 ? 0xFF0D1112 : 0;
            if(background != 0) getRenderAdapter().drawRect(ctx, x, entryTop + 1, width, itemHeight - 2, background);

            int textX = x + 4;
            if(iconId != null)
            {
                getRenderAdapter().drawItem(ctx, new CoreItemStack(iconId, 1), textX, entryTop + (itemHeight - 16) / 2, 1F);
                textX += 20;
            }

            int color = selected ? 0xFFFFFFFF : hovered ? 0xFFEFEFEF : 0xFFDDE6EE;
            String text = trimToWidth(label, x + width - textX - 2);
            getRenderAdapter().drawText(ctx, text, textX, entryTop + (itemHeight - getRenderAdapter().getFontHeight()) / 2 + 1, color);
        }

        /**
         * Lines shown when hovering the entry. Labels that don't fit are shown in full by default.
         */
        public List<String> getTooltip(int width)
        {
            int labelSpace = width - 8 - (iconId != null ? 20 : 0);
            return getRenderAdapter().getTextWidth(label) > labelSpace ? List.of(label) : List.of();
        }

        public void renderForeground(RenderContext ctx, int index, int x, int y, int width, int itemHeight, int mouseX, int mouseY, boolean selected, boolean hovered)
        {
            List<String> tooltip = getTooltip(width);
            if(hovered && !tooltip.isEmpty()) getRenderAdapter().drawTooltips(ctx, tooltip, mouseX, mouseY);
        }

        protected static String trimToWidth(String text, int maxWidth)
        {
            RenderAdapter adapter = getRenderAdapter();
            if(adapter.getTextWidth(text) <= maxWidth) return text;

            String ellipsis = "...";
            int end = text.length();
            while(end > 0 && adapter.getTextWidth(text.substring(0, end) + ellipsis) > maxWidth) end--;
            return text.substring(0, end) + ellipsis;
        }

        protected static RenderAdapter getRenderAdapter()
        {
            return CraftCreatorAPI.get().getPlatform().getRenderAdapter();
        }
    }
}
