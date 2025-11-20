package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;


/**
 * Loader-agnostic render adapter. All rendering calls receive a core RenderContext instance.
 * Platform adapters cast the RenderContext to their concrete context type internally.
 */
public interface RenderAdapter
{
    /**
     * @return The current (scaled) screen width in pixels.
     */
    int getScreenWidth();

    /**
     * @return The current (scaled) screen height in pixels.
     */
    int getScreenHeight();

    Pair<Integer, CoreItemStack> getItemStackUnderMouse(double mouseX, double mouseY);

    void drawTexture(RenderContext ctx,
                     Identifier textureId,
                     int x, int y, int width, int height,
                     int textureWidth, int textureHeight,
                     int textureX, int textureY, int widthInTexture, int heightInTexture);

    void drawText(RenderContext ctx, String text, int x, int y, int color);

    void drawRect(RenderContext ctx, int x, int y, int width, int height, int argb);

    void drawItem(RenderContext ctx, CoreItemStack item, int x, int y);

    void bindTexture(RenderContext ctx, Identifier backgroundTexture);
}