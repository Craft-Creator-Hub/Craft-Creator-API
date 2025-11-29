package fr.en0ri4n.craftcreator.api.ui.elements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreBounds
{
    private int x;
    private int y;
    private int width;
    private int height;

    public static CoreBounds ofPos(int x, int y)
    {
        return new CoreBounds(x, y, 0, 0);
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

    public static CoreBounds ofSize(int width, int height) {
        return new CoreBounds(0, 0, width, height);
    }

    public static CoreBounds of(int x, int y, int width, int height) {
        return new CoreBounds(x, y, width, height);
    }  public static CoreBounds ofVertices(int right, int top, int bottom, int left) {
        return new CoreBounds(right, top, left - right, bottom - top);
    }

    public static CoreBounds square(int x, int y, int size) {
        return new CoreBounds(x, y, size, size);
    }

    public static CoreBounds fromRight(int right, int y, int width, int height) {
        return new CoreBounds(right - width, y, width, height);
    }

    public static CoreBounds centerScreen(CoreBounds bounds, int screenWidth, int screenHeight) {
        int width = bounds.getWidth();
        int height = bounds.getHeight();
        int x = (screenWidth - width) / 2;
        int y = (screenHeight - height) / 2;
        return new CoreBounds(x, y, width, height);
    }
}
