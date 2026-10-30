package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;

import java.util.List;


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

    void drawText(RenderContext ctx, String text, boolean shadow, int x, int y, int color);

    default void drawText(RenderContext ctx, String text, int x, int y, int color) {
        drawText(ctx, text, true, x, y, color);
    }

    void drawRect(RenderContext ctx, int x, int y, int width, int height, int argb);

    void drawItem(RenderContext ctx, CoreItemStack item, int x, int y, float scale);

    void drawTooltip(RenderContext ctx, String tooltip, int mouseX, int mouseY);
    
    void drawTooltips(RenderContext ctx, List<String> tooltips, int mouseX, int mouseY);

    void drawItemTooltip(RenderContext ctx, CoreItemStack item, int mouseX, int mouseY);

    void drawItemTooltip(RenderContext ctx, CoreItemStack item, List<String> additionalTooltips, int mouseX, int mouseY);

    int getTextWidth(String label);

    int getFontHeight();

    void startScale(RenderContext ctx, float scaleX, float scaleY);

    void endScale(RenderContext ctx);

    /**
     * Restrict rendering to the given rectangle (in scaled GUI coordinates) until {@link #disableScissor(RenderContext)} is called.
     */
    void enableScissor(RenderContext ctx, int x, int y, int width, int height);

    void disableScissor(RenderContext ctx);

    /**
     * Draw everything (shapes, text and items) on a layer above the current one, until {@link #popLayer(RenderContext)}.
     * Used by tooltips, which must cover items.
     */
    void pushLayer(RenderContext ctx, float z);

    void popLayer(RenderContext ctx);

    /**
     * @return the translated display name of the item, e.g. "Oak Planks"
     */
    String getItemName(CoreItemStack item);
}