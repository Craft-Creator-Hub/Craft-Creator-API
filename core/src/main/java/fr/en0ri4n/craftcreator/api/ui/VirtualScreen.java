package fr.en0ri4n.craftcreator.api.ui;

/**
 * Loader-agnostic drawing surface.
 * Implemented per loader to map to actual rendering APIs.
 */
public interface VirtualScreen {
    int getWidth();
    int getHeight();

    void drawRect(int x, int y, int w, int h, int argb);
    void drawText(String text, int x, int y, int color);
    void drawItem(String itemId, int x, int y); // or use Identifier later
}