package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Draws buttons with textures/gui/button.png: a raised face with a lip at the bottom, pushed in when pressed or selected.<br>
 * Panels keep {@link CoreScreenDefinition#GUI_TEXTURE}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CoreButtonStyle
{
    public static final Identifier BUTTON_TEXTURE = Identifier.fromMod("textures/gui/button.png");
    private static final int TEXTURE_HEIGHT = 64;

    public enum State
    {
        NORMAL(0, 0),
        HOVERED(1, 0),
        /** Pushed in with a gold outline: the selected tab, the current value... */
        PRESSED(2, 1),
        DISABLED(3, 0);

        private final int tile;
        /** How far the content moves down so it stays centered on the face. */
        private final int contentOffsetY;

        State(int tile, int contentOffsetY)
        {
            this.tile = tile;
            this.contentOffsetY = contentOffsetY;
        }

        public int getContentOffsetY()
        {
            return contentOffsetY;
        }

        public static State of(boolean enabled, boolean pressed, boolean hovered)
        {
            return !enabled ? DISABLED : pressed ? PRESSED : hovered ? HOVERED : NORMAL;
        }
    }

    /**
     * Draw a button background. Buttons should be at least 10 pixels high.
     */
    public static void draw(RenderContext ctx, int x, int y, int width, int height, State state)
    {
        CoreScreenDefinition.renderNineSlice(ctx, BUTTON_TEXTURE, TEXTURE_HEIGHT, state.tile, x, y, width, height);
    }

    public static void draw(RenderContext ctx, Core2DBounds bounds, State state)
    {
        draw(ctx, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), state);
    }

    /**
     * @return the y to draw a text of the given height centered on the button's face
     */
    public static int textY(Core2DBounds bounds, int fontHeight, State state)
    {
        // The lip at the bottom raises the face by 1 pixel, pressing it lowers it by 1
        return bounds.getVerticalCenter(fontHeight) + state.contentOffsetY;
    }
}
