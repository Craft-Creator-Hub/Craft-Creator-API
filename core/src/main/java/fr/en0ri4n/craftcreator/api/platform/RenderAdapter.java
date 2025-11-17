package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;


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

    void renderTexture(RenderContext ctx,
                       Identifier textureId,
                       float x, float y, float width, float height,
                       int textureWidth, int textureHeight,
                       float u, float v, float uWidth, float vHeight,
                       float z);

    void renderText(RenderContext ctx, String text, float x, float y, int color, float z);

    void renderRect(RenderContext ctx, float x, float y, float width, float height, int argb, float z);

    void renderItem(RenderContext ctx, Identifier itemId, float x, float y, float z);

    void bindTexture(RenderContext ctx, Identifier backgroundTexture);
}