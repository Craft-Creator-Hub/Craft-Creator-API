package fr.en0ri4n.craftcreator.api.ui.elements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Core2DBounds
{
    private int x;
    private int y;
    private int width;
    private int height;

    public static Core2DBounds ofPos(int x, int y)
    {
        return new Core2DBounds(x, y, 0, 0);
    }

    public int getScaledX(float scale)
    {
        return Math.round(x / scale);
    }

    public int getScaledY(float scale)
    {
        return Math.round(y / scale);
    }

    public int getScaledX(int offset, float scale)
    {
        return Math.round((x + offset) / scale);
    }

    public int getScaledY(int offset, float scale)
    {
        return Math.round((y + offset) / scale);
    }

    public int getX(int offset)
    {
        return x + offset;
    }

    public int getY(int offset)
    {
        return y + offset;
    }

    public int getWidth(int offset)
    {
        return width + offset;
    }

    public int getHeight(int offset)
    {
        return height + offset;
    }

    public int getRight()
    {
        return x + width;
    }

    public int getRight(int offset)
    {
        return getRight() + offset;
    }

    public int getBottom()
    {
        return y + height;
    }

    public int getBottom(int offset)
    {
        return getBottom() + offset;
    }

    public boolean contains(int px, int py)
    {
        return px >= x && px < getRight() && py >= y && py < getBottom();
    }

    public int getHorizontalCenter() {
        return x + width / 2;
    }

    public int getVerticalCenter() {
        return y + height / 2;
    }

    public int getHorizontalCenter(int offset) {
        return getHorizontalCenter() - offset / 2;
    }

    public int getVerticalCenter(int offset) {
        return getVerticalCenter() - offset / 2;
    }

    public static Core2DBounds ofSize(int width, int height) {
        return new Core2DBounds(0, 0, width, height);
    }

    public static Core2DBounds of(int x, int y, int width, int height) {
        return new Core2DBounds(x, y, width, height);
    }  public static Core2DBounds ofVertices(int right, int top, int bottom, int left) {
        return new Core2DBounds(right, top, left - right, bottom - top);
    }

    public static Core2DBounds square(int x, int y, int size) {
        return new Core2DBounds(x, y, size, size);
    }

    public static Core2DBounds fromRight(int right, int y, int width, int height) {
        return new Core2DBounds(right - width, y, width, height);
    }

    public static Core2DBounds centerScreen(Core2DBounds bounds, int screenWidth, int screenHeight) {
        int width = bounds.getWidth();
        int height = bounds.getHeight();
        int x = (screenWidth - width) / 2;
        int y = (screenHeight - height) / 2;
        return new Core2DBounds(x, y, width, height);
    }
}
