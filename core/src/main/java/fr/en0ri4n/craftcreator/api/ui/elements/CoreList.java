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

/**
 * Core, platform-agnostic list widget.
 * <p>
 * Rendering is done entirely via RenderAdapter / RenderContext passed from the platform.
 * Input events are also handled via simple methods (mouseClicked/mouseScrolled) so the
 * platform screen can forward events into this core widget.
 */
public class CoreList extends CoreUiElement
{
    public static final int DEFAULT_ITEM_HEIGHT = 20;

    @Getter
    private final List<Entry> entries = new ArrayList<>();
    private int x, y, width, height;
    @Getter
    private int itemHeight = DEFAULT_ITEM_HEIGHT;

    // scroll offset in pixels (0..maxScroll)
    @Getter
    private int scrollOffset = 0;

    // selected / hovered indices (hover is computed in render; stored for convenience)
    @Getter
    private int selectedIndex = -1;
    private int hoveredIndex = -1;

    // optional callback when selection changes
    private Consumer<Optional<Entry>> selectionListener = e ->
    {
    };

    public CoreList(int x, int y, int width, int height, int itemHeight, List<Entry> initialEntries)
    {
        super(CoreUiElementType.LIST, x, y, width, height, "");
        setEntries(initialEntries);
        setBounds(x, y, width, height);
        this.itemHeight = itemHeight;
    }

    public void setBounds(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setEntries(List<Entry> newEntries)
    {
        this.entries.clear();
        if(newEntries != null)
        {
            this.entries.addAll(newEntries);
        }
        clampScroll();
        if(selectedIndex >= entries.size())
        {
            setSelectedIndex(-1);
        }
    }

    public void setItemHeight(int itemHeight)
    {
        this.itemHeight = Math.max(4, itemHeight);
    }

    public void addEntry(Entry e)
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

    public Optional<Entry> getSelected()
    {
        if(selectedIndex < 0 || selectedIndex >= entries.size()) return Optional.empty();
        return Optional.of(entries.get(selectedIndex));
    }

    public void setSelectionListener(Consumer<Optional<Entry>> listener)
    {
        this.selectionListener = listener == null ? e ->
        {
        } : listener;
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

    public void scrollBy(int delta)
    {
        scrollOffset += delta;
        clampScroll();
    }

    public void setScrollOffset(int offset)
    {
        this.scrollOffset = offset;
        clampScroll();
    }

    private void clampScroll()
    {
        int max = Math.max(0, entries.size() * itemHeight - height);
        if(scrollOffset < 0) scrollOffset = 0;
        if(scrollOffset > max) scrollOffset = max;
    }

    private void ensureIndexVisible(int idx)
    {
        if(idx < 0 || idx >= entries.size()) return;
        int topOfIdx = idx * itemHeight;
        int bottomOfIdx = topOfIdx + itemHeight;
        if(topOfIdx < scrollOffset)
        {
            scrollOffset = topOfIdx;
        }
        else if(bottomOfIdx > scrollOffset + height)
        {
            scrollOffset = bottomOfIdx - height;
        }
        clampScroll();
    }

    /**
     * Render widget using the provided RenderAdapter and RenderContext.
     * <p>
     * Platform screens should create a concrete RenderContext (e.g. ForgeRenderContext)
     * and pass it here together with the platform's RenderAdapter implementation.
     *
     * @param ctx          platform render context (opaque)
     * @param mouseX       mouse x relative to the screen
     * @param mouseY       mouse y relative to the screen
     */
    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float pPartialTick)
    {
        if(ctx == null) return;

        // Draw background panel
        getRenderAdapter().drawRect(ctx, x, y, width, height, 0xFF1F2933);
        getRenderAdapter().drawRect(ctx, x + 4, y + 4, width - 8, height - 8, 0xFF0F1416);

        // compute visible range
        int firstIndex = scrollOffset / itemHeight;
        int lastIndex = Math.min(entries.size() - 1, (scrollOffset + height - 1) / itemHeight);

        // compute mouse-relative to widget
        hoveredIndex = -1;
        if(mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height)
        {
            int relY = mouseY - y + scrollOffset;
            int idx = relY / itemHeight;
            if(idx >= 0 && idx < entries.size()) hoveredIndex = idx;
        }

        // render entries
        for(int i = firstIndex; i <= lastIndex; i++)
        {
            Entry e = entries.get(i);
            int entryTop = y + (i * itemHeight - scrollOffset);
            boolean hovered = (i == hoveredIndex);
            boolean selected = (i == selectedIndex);

            e.render(ctx, i, x, entryTop, width, itemHeight, selected, hovered);
        }

        // draw scrollbar (if needed)
        drawScrollbar(ctx);
    }

    private void drawScrollbar(RenderContext ctx)
    {
        int contentHeight = entries.size() * itemHeight;
        if(contentHeight <= height) return;

        int barX = x + width - 10;
        int barY = y + 4;
        int barW = 6;
        int barH = height - 8;

        // track background
        getRenderAdapter().drawRect(ctx, barX, barY, barW, barH, 0x66191919);

        // thumb height proportional
        float ratio = (float) height / (float) contentHeight;
        int thumbH = Math.max(8, (int) (barH * ratio));
        int maxThumbY = barH - thumbH;
        int thumbY = (int) ((float) scrollOffset / Math.max(1, contentHeight - height) * maxThumbY);

        getRenderAdapter().drawRect(ctx, barX, barY + thumbY, barW, thumbH, 0xAA2B8AE6);
    }

    /**
     * Handle mouse click forwarded from platform screen.
     *
     * @param mouseX absolute mouse x
     * @param mouseY absolute mouse y
     * @param button mouse button (0 left, 1 right, etc)
     * @return true if the list handled the click (selection or entry click)
     */
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height)
        {
            return false;
        }
        int relY = mouseY - y + scrollOffset;
        int idx = relY / itemHeight;
        if(idx < 0 || idx >= entries.size()) return false;

        setSelectedIndex(idx);

        Entry e = entries.get(idx);
        if(e.onClick != null)
        {
            e.onClick.accept(e);
            return true;
        }
        return true;
    }

    /**
     * Handle mouse wheel scrolling forwarded from platform.
     *
     * @param delta positive => scroll up, negative => scroll down
     * @return true if the widget reacted to the scroll
     */
    public boolean mouseScrolled(double delta)
    {
        if(height <= 0) return false;
        int scrollAmount = (int) (-delta * itemHeight); // typical inversion
        if(scrollAmount == 0) scrollAmount = (int) Math.copySign(1, -delta) * itemHeight;
        scrollBy(scrollAmount);
        return true;
    }

    /**
     * Simple list entry model.
     * Platform-independent: icon is an Identifier and rendering is delegated to RenderAdapter.
     */
    @Getter
    public static class Entry
    {
        private final String label;
        private final String value;
        private final Identifier iconId;
        private final Consumer<Entry> onClick;

        public Entry(String label, String value, Identifier iconId, Consumer<Entry> onClick)
        {
            this.label = Objects.requireNonNull(label);
            this.value = value;
            this.iconId = iconId;
            this.onClick = onClick;
        }

        public Entry(String label, String value, Consumer<Entry> onClick)
        {
            this(label, value, null, onClick);
        }

        public void render(RenderContext ctx, int index, int x, int entryTop, int width, int itemHeight, boolean selected, boolean hovered)
        {
            // draw row background (alternate shading)
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

            // draw icon if present (left side)
            float iconX = x + 10f;
            float iconY = entryTop + (itemHeight - 16) / 2f;
            if(iconId != null)
            {
                getRenderAdapter().drawItem(ctx, new CoreItemStack(iconId, 1), (int) iconX, (int) iconY, 1F);
            }

            // draw text
            float textX = x + 10f + (iconId != null ? 20f : 0f);
            float textY = entryTop + (itemHeight - 8) / 2f;
            int color = selected ? 0xFFFFFFFF : (hovered ? 0xFFEFEFEF : 0xFFDDE6EE);
            getRenderAdapter().drawText(ctx, label, (int) textX, (int) textY, color);
        }

        private RenderAdapter getRenderAdapter()
        {
            return CraftCreatorAPI.get().getPlatform().getRenderAdapter();
        }
    }
}