package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.Getter;

/**
 * Vertical scroll state + scrollbar shared by scrollable elements.<br>
 * The owner gives the track bounds (where the bar is drawn) and the total content height, the scrollbar handles
 * clamping, wheel, click-to-jump and thumb dragging.
 */
public class CoreScrollbar
{
    public static final int WIDTH = 6;
    private static final int MIN_THUMB_HEIGHT = 8;
    private static final int TRACK_COLOR = 0x66191919;
    private static final int THUMB_COLOR = 0xAA2B8AE6;
    private static final int THUMB_HOVER_COLOR = 0xFF4FA3F0;

    private final Core2DBounds track = Core2DBounds.of(0, 0, WIDTH, 0);
    private int contentHeight;

    @Getter
    private int scrollOffset;
    @Getter
    private boolean dragging;
    /** Distance between the mouse and the top of the thumb when the drag started. */
    private int dragGrabOffset;

    public void update(int trackX, int trackY, int trackHeight, int contentHeight)
    {
        track.setX(trackX);
        track.setY(trackY);
        track.setHeight(Math.max(0, trackHeight));
        this.contentHeight = Math.max(0, contentHeight);
        clamp();
    }

    public boolean isNeeded()
    {
        return contentHeight > track.getHeight();
    }

    public int getMaxScroll()
    {
        return Math.max(0, contentHeight - track.getHeight());
    }

    public void setScrollOffset(int scrollOffset)
    {
        this.scrollOffset = scrollOffset;
        clamp();
    }

    public void scrollBy(int delta)
    {
        setScrollOffset(scrollOffset + delta);
    }

    /**
     * Make sure the [top, bottom] range of the content is visible, scrolling as little as possible.
     */
    public void ensureVisible(int top, int bottom)
    {
        if(top < scrollOffset) setScrollOffset(top);
        else if(bottom > scrollOffset + track.getHeight()) setScrollOffset(bottom - track.getHeight());
    }

    private void clamp()
    {
        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
    }

    private int getThumbHeight()
    {
        if(contentHeight <= 0) return track.getHeight();
        return Math.min(track.getHeight(), Math.max(MIN_THUMB_HEIGHT, track.getHeight() * track.getHeight() / contentHeight));
    }

    private int getThumbY()
    {
        int freeSpace = track.getHeight() - getThumbHeight();
        int maxScroll = getMaxScroll();
        return track.getY() + (maxScroll == 0 ? 0 : scrollOffset * freeSpace / maxScroll);
    }

    public boolean isOver(int mouseX, int mouseY)
    {
        return isNeeded() && track.contains(mouseX, mouseY);
    }

    public void render(RenderContext ctx, RenderAdapter adapter, int mouseX, int mouseY)
    {
        if(!isNeeded()) return;

        adapter.drawRect(ctx, track.getX(), track.getY(), track.getWidth(), track.getHeight(), TRACK_COLOR);
        boolean highlighted = dragging || isOver(mouseX, mouseY);
        adapter.drawRect(ctx, track.getX(), getThumbY(), track.getWidth(), getThumbHeight(), highlighted ? THUMB_HOVER_COLOR : THUMB_COLOR);
    }

    /**
     * Start dragging the thumb. Clicking the track outside the thumb centers the thumb on the mouse first.
     *
     * @return true if the click was on the scrollbar
     */
    public boolean mouseClicked(int mouseX, int mouseY)
    {
        if(!isOver(mouseX, mouseY)) return false;

        int thumbY = getThumbY();
        if(mouseY < thumbY || mouseY >= thumbY + getThumbHeight())
        {
            dragGrabOffset = getThumbHeight() / 2;
            moveThumbTo(mouseY);
        }
        else
        {
            dragGrabOffset = mouseY - thumbY;
        }

        dragging = true;
        return true;
    }

    public boolean mouseDragged(int mouseY)
    {
        if(!dragging) return false;
        moveThumbTo(mouseY);
        return true;
    }

    public boolean mouseReleased()
    {
        boolean wasDragging = dragging;
        dragging = false;
        return wasDragging;
    }

    private void moveThumbTo(int mouseY)
    {
        int freeSpace = track.getHeight() - getThumbHeight();
        if(freeSpace <= 0) return;

        int thumbTop = mouseY - dragGrabOffset - track.getY();
        setScrollOffset(thumbTop * getMaxScroll() / freeSpace);
    }
}
