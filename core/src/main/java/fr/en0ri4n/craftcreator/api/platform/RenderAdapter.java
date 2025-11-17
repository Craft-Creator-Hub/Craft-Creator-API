package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.utils.Identifier;


/**
 * Loader-agnostic render adapter. All rendering calls receive a core RenderContext instance.
 * Platform adapters cast the RenderContext to their concrete context type internally.
 */
public interface RenderAdapter
{
    int getScreenWidth();

    int getScreenHeight();

    void renderTexture(Identifier textureId,
                       float x, float y, float width, float height,
                       int textureWidth, int textureHeight,
                       float u, float v, float uWidth, float vHeight,
                       float z);

    void renderText(String text, float x, float y, int color, float z);

    void renderRect(float x, float y, float width, float height, int argb, float z);

    void renderItem(Identifier itemId, float x, float y, float z);

    void bindTexture(Identifier backgroundTexture);
}