package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-only view of a pretty-printed json, scrolling both ways, with line numbers and colored keys, strings, numbers and literals.
 */
public class CoreJsonView extends CoreUiElement
{
    private static final int PADDING = 4;
    private static final int LINE_HEIGHT = 10;
    private static final int GUTTER_GAP = 6;
    private static final int SCROLL_LINES = 3;
    private static final int H_BAR_HEIGHT = 6;
    private static final int H_SCROLL_STEP = 24;
    private static final int MIN_THUMB_WIDTH = 8;

    private static final int COLOR_KEY = 0xFF9CDCFE;
    private static final int COLOR_STRING = 0xFFCE9178;
    private static final int COLOR_NUMBER = 0xFFB5CEA8;
    private static final int COLOR_LITERAL = 0xFF569CD6;
    private static final int COLOR_PUNCTUATION = 0xFFD4D4D4;
    private static final int COLOR_LINE_NUMBER = 0xFF6A6A6A;

    @Getter
    private final String json;
    private final List<List<Token>> lines = new ArrayList<>();
    private final CoreScrollbar scrollbar = new CoreScrollbar();
    private int horizontalScroll;
    private boolean hDragging;
    /** Distance between the mouse and the left of the horizontal thumb when the drag started */
    private int hGrabOffset;
    /** Width of the longest line, -1 until measured */
    private int textWidth = -1;

    private record Token(String text, int color) {}

    public CoreJsonView(String id, int x, int y, int width, int height, String json)
    {
        super(CoreUiElementType.LIST, id, x, y, width, height, null);
        this.json = json == null ? "" : json;
        for(String line : this.json.replace("\t", "  ").split("\n", -1))
            lines.add(tokenize(line));
    }

    /**
     * Split a line of pretty-printed json into colored tokens. A string followed by ':' is a key.
     */
    private static List<Token> tokenize(String line)
    {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while(i < line.length())
        {
            char c = line.charAt(i);
            if(c == '"')
            {
                int end = i + 1;
                while(end < line.length() && line.charAt(end) != '"')
                    end += line.charAt(end) == '\\' ? 2 : 1;
                end = Math.min(end + 1, line.length());
                String text = line.substring(i, end);
                boolean key = line.substring(end).trim().startsWith(":");
                tokens.add(new Token(text, key ? COLOR_KEY : COLOR_STRING));
                i = end;
            }
            else if(c == '-' || Character.isDigit(c))
            {
                int end = i + 1;
                while(end < line.length() && "0123456789.eE+-".indexOf(line.charAt(end)) >= 0) end++;
                tokens.add(new Token(line.substring(i, end), COLOR_NUMBER));
                i = end;
            }
            else if(Character.isLetter(c))
            {
                int end = i + 1;
                while(end < line.length() && Character.isLetter(line.charAt(end))) end++;
                tokens.add(new Token(line.substring(i, end), COLOR_LITERAL));
                i = end;
            }
            else
            {
                int end = i + 1;
                while(end < line.length() && "\"-0123456789".indexOf(line.charAt(end)) < 0 && !Character.isLetter(line.charAt(end))) end++;
                tokens.add(new Token(line.substring(i, end), COLOR_PUNCTUATION));
                i = end;
            }
        }
        return tokens;
    }

    private int getContentX(RenderAdapter adapter)
    {
        return getBounds().getX(PADDING + gutterWidth(adapter) + GUTTER_GAP);
    }

    private int gutterWidth(RenderAdapter adapter)
    {
        return adapter.getTextWidth(String.valueOf(lines.size()));
    }

    private int getContentY()
    {
        return getBounds().getY(PADDING);
    }

    /** Height of the text area: the horizontal bar takes the bottom when it is needed */
    private int getContentHeight()
    {
        return getBounds().getHeight() - PADDING * 2 - (isHorizontalNeeded() ? H_BAR_HEIGHT + 2 : 0);
    }

    /** Right edge of the text area: the vertical bar takes the right side when it is needed */
    private int getContentRight()
    {
        return getBounds().getRight(-PADDING - (scrollbar.isNeeded() ? CoreScrollbar.WIDTH + 2 : 0));
    }

    private int getViewWidth()
    {
        return getContentRight() - getContentX(getRenderAdapter());
    }

    /** Width of the longest line, measured once */
    private int getTextWidth()
    {
        if(textWidth < 0)
            textWidth = lines.stream().mapToInt(line -> line.stream().mapToInt(token -> getRenderAdapter().getTextWidth(token.text())).sum()).max().orElse(0);
        return textWidth;
    }

    private boolean isHorizontalNeeded()
    {
        return getTextWidth() > getBounds().getRight(-PADDING) - getContentX(getRenderAdapter());
    }

    private int getMaxHorizontalScroll()
    {
        return Math.max(0, getTextWidth() - getViewWidth());
    }

    private void setHorizontalScroll(int offset)
    {
        horizontalScroll = Math.max(0, Math.min(offset, getMaxHorizontalScroll()));
    }

    /* The horizontal bar runs under the text, from the gutter to the vertical bar */

    private int getHTrackX()
    {
        return getContentX(getRenderAdapter());
    }

    private int getHTrackY()
    {
        return getBounds().getBottom(-PADDING - H_BAR_HEIGHT);
    }

    private int getHThumbWidth()
    {
        int track = getViewWidth();
        return Math.min(track, Math.max(MIN_THUMB_WIDTH, track * track / Math.max(1, getTextWidth())));
    }

    private int getHThumbX()
    {
        int free = getViewWidth() - getHThumbWidth();
        int max = getMaxHorizontalScroll();
        return getHTrackX() + (max == 0 ? 0 : horizontalScroll * free / max);
    }

    private boolean isOverHorizontalBar(int mouseX, int mouseY)
    {
        return isHorizontalNeeded() && mouseX >= getHTrackX() && mouseX < getHTrackX() + getViewWidth()
                && mouseY >= getHTrackY() - 1 && mouseY < getHTrackY() + H_BAR_HEIGHT + 1;
    }

    private void moveHThumbTo(int mouseX)
    {
        int free = getViewWidth() - getHThumbWidth();
        if(free <= 0) return;
        setHorizontalScroll((mouseX - hGrabOffset - getHTrackX()) * getMaxHorizontalScroll() / free);
    }

    private void layout()
    {
        scrollbar.update(getBounds().getRight(-PADDING - CoreScrollbar.WIDTH), getContentY(), getContentHeight(), lines.size() * LINE_HEIGHT);
        setHorizontalScroll(horizontalScroll);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        if(!isVisible()) return;
        RenderAdapter adapter = getRenderAdapter();
        layout();

        CoreTheme.drawInset(adapter, ctx, getBounds(), false);
        int gutterRight = getBounds().getX(PADDING + gutterWidth(adapter));
        adapter.drawRect(ctx, gutterRight + GUTTER_GAP / 2, getBounds().getY(2), 1, getBounds().getHeight() - 4, CoreTheme.INSET_SHADOW);

        int first = scrollbar.getScrollOffset() / LINE_HEIGHT;
        int contentX = getContentX(adapter);

        // Line numbers only scroll up and down
        adapter.enableScissor(ctx, getBounds().getX(1), getContentY(), gutterRight - getBounds().getX(1), getContentHeight());
        for(int index = first; index < lines.size(); index++)
        {
            int y = getContentY() + index * LINE_HEIGHT - scrollbar.getScrollOffset();
            if(y > getContentY() + getContentHeight()) break;
            String number = String.valueOf(index + 1);
            adapter.drawText(ctx, number, false, gutterRight - adapter.getTextWidth(number), y + 1, COLOR_LINE_NUMBER);
        }
        adapter.disableScissor(ctx);

        // The text scrolls both ways
        adapter.enableScissor(ctx, contentX, getContentY(), getViewWidth(), getContentHeight());
        for(int index = first; index < lines.size(); index++)
        {
            int y = getContentY() + index * LINE_HEIGHT - scrollbar.getScrollOffset();
            if(y > getContentY() + getContentHeight()) break;
            int x = contentX - horizontalScroll;
            for(Token token : lines.get(index))
            {
                adapter.drawText(ctx, token.text(), false, x, y + 1, token.color());
                x += adapter.getTextWidth(token.text());
            }
        }
        adapter.disableScissor(ctx);

        scrollbar.render(ctx, adapter, mouseX, mouseY);
        if(isHorizontalNeeded())
        {
            adapter.drawRect(ctx, getHTrackX(), getHTrackY(), getViewWidth(), H_BAR_HEIGHT, CoreTheme.INSET_SHADOW);
            boolean highlighted = hDragging || isOverHorizontalBar(mouseX, mouseY);
            adapter.drawRect(ctx, getHThumbX(), getHTrackY(), getHThumbWidth(), H_BAR_HEIGHT, highlighted ? CoreTheme.THUMB_HOVER : CoreTheme.THUMB);
            adapter.drawRect(ctx, getHThumbX(), getHTrackY(), getHThumbWidth(), 1, CoreTheme.THUMB_HOVER);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(!isVisible()) return false;
        layout();
        if(isOverHorizontalBar(mouseX, mouseY))
        {
            int thumbX = getHThumbX();
            if(mouseX < thumbX || mouseX >= thumbX + getHThumbWidth())
            {
                hGrabOffset = getHThumbWidth() / 2;
                moveHThumbTo(mouseX);
            }
            else
            {
                hGrabOffset = mouseX - thumbX;
            }
            hDragging = true;
            return true;
        }
        return scrollbar.mouseClicked(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int button)
    {
        if(hDragging)
        {
            moveHThumbTo(mouseX);
            return true;
        }
        return scrollbar.mouseDragged(mouseY);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        boolean wasDragging = hDragging;
        hDragging = false;
        return scrollbar.mouseReleased() || wasDragging;
    }

    /**
     * The wheel scrolls down and up; with Shift held, or over the horizontal bar, it scrolls sideways.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if(!isVisible() || !getBounds().contains((int) mouseX, (int) mouseY)) return false;
        layout();
        if(isHorizontalNeeded() && (getUiAdapter().isShiftKeyDown() || isOverHorizontalBar((int) mouseX, (int) mouseY)))
            setHorizontalScroll(horizontalScroll + (delta > 0 ? -H_SCROLL_STEP : H_SCROLL_STEP));
        else
            scrollbar.scrollBy(delta > 0 ? -LINE_HEIGHT * SCROLL_LINES : LINE_HEIGHT * SCROLL_LINES);
        return true;
    }
}
