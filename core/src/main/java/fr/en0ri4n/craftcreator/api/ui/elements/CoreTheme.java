package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Colors shared by every core widget, taken from the GUI texture (textures/gui/base.png) so drawn widgets match textured ones.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CoreTheme
{
    /** Outline of every panel and widget. */
    public static final int BORDER = 0xFF171717;
    /** Recessed areas: lists, text inputs, scrollbar tracks. */
    public static final int INSET = 0xFF262626;
    public static final int INSET_SHADOW = 0xFF1C1C1C;
    public static final int ROW_ALT = 0xFF2B2B2B;
    public static final int ROW_HOVER = 0xFF3E3E3E;
    public static final int ROW_SELECTED = 0xFF545454;
    public static final int ACCENT = 0xFFFFC94A;
    public static final int THUMB = 0xFF5E5E5E;
    public static final int THUMB_HOVER = 0xFF828282;
    public static final int FOCUS_BORDER = 0xFF828282;

    public static final int TEXT = 0xFFE0E0E0;
    public static final int TEXT_BRIGHT = 0xFFFFFFFF;
    public static final int TEXT_MUTED = 0xFF8A8A8A;
    public static final int TEXT_DISABLED = 0xFF6A6A6A;
    public static final int TEXT_ERROR = 0xFFFF6B6B;
    public static final int TEXT_SUCCESS = 0xFF7BE07B;
    public static final int TEXT_TITLE = 0xFFFFC94A;

    private static final int ARROW_HALF_WIDTH = 3;

    /**
     * Draw a small triangle pointing down or up, made of rectangles (the font has no arrow glyphs).
     *
     * @param centerX x of the tip
     * @param centerY vertical center of the triangle
     */
    public static void drawArrow(RenderAdapter adapter, RenderContext ctx, int centerX, int centerY, boolean down, int color)
    {
        int rows = ARROW_HALF_WIDTH + 1;
        int top = centerY - rows / 2;
        for(int row = 0; row < rows; row++)
        {
            // Widest row at the top for a down arrow, at the bottom for an up arrow
            int halfWidth = down ? ARROW_HALF_WIDTH - row : row;
            adapter.drawRect(ctx, centerX - halfWidth, top + row, halfWidth * 2 + 1, 1, color);
        }
    }

    /**
     * Draw a recessed box: 1px border, a darker top/left shadow and the inset color.
     */
    public static void drawInset(RenderAdapter adapter, RenderContext ctx, Core2DBounds bounds, boolean focused)
    {
        adapter.drawRect(ctx, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), focused ? FOCUS_BORDER : BORDER);
        adapter.drawRect(ctx, bounds.getX(1), bounds.getY(1), bounds.getWidth(-2), bounds.getHeight(-2), INSET_SHADOW);
        adapter.drawRect(ctx, bounds.getX(2), bounds.getY(2), bounds.getWidth(-3), bounds.getHeight(-3), INSET);
    }
}
