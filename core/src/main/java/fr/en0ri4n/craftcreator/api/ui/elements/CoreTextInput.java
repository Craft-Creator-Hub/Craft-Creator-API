package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

/**
 * Core representation of a single-line text input.
 */
@Getter
public class CoreTextInput extends CoreUiElement
{
    @Setter
    private String value;

    private final TextInputType inputType;
    private final String placeholder;
    private final String label;

    private final int maxLength;
    private boolean isFocused;
    private int cursorPosition;

    public CoreTextInput(String id, TextInputType inputType, int x, int y, int width, int height, String label, String value, String placeholder, int maxLength, String tooltip)
    {
        super(CoreUiElementType.TEXT_INPUT, id, x, y, width, height, tooltip);
        this.inputType = inputType;
        this.value = value;
        this.label = label;
        this.placeholder = placeholder;
        this.maxLength = maxLength;
    }

    public CoreTextInput(String id, TextInputType type, int x, int y, int width, int height, String label, String value, String placeholder, String tooltip)
    {
        this(id, type, x, y, width, height, label, value, placeholder, Short.MAX_VALUE, tooltip);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(isFocused())
        {
            if(keyCode == 259) // backspace
            {
                if(cursorPosition > 0 && !value.isEmpty())
                {
                    value = value.substring(0, cursorPosition - 1) + value.substring(cursorPosition);
                    cursorPosition--;
                }
            }
            else if(keyCode == 262) // right arrow
            {
                if(cursorPosition < value.length())
                    cursorPosition++;
            }
            else if(keyCode == 263) // left arrow
            {
                if(cursorPosition > 0)
                    cursorPosition--;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if(isFocused())
        {
            if(getInputType() == TextInputType.INTEGER && !Character.isDigit(codePoint))
                return true; // ignore non-digit input for integer type
            if(getInputType() == TextInputType.FLOAT)
                if(!Character.isDigit(codePoint) && codePoint != '.' || (codePoint == '.' && value.contains(".")))
                    return true; // ignore non-digit and non-dot input for float type

            String newValue = value.substring(0, cursorPosition) + codePoint + value.substring(cursorPosition);
            if(newValue.length() <= maxLength)
            {
                value = newValue;
                cursorPosition++;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            isFocused = true;
            cursorPosition = value.length();
            return true;
        }
        else
        {
            isFocused = false;
            return false;
        }
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        // Draw label
        float scale = 6F / 9F;
        getRenderAdapter().startScale(ctx, scale, scale);
        getRenderAdapter().drawText(ctx, getLabel(), getBounds().getScaledX(scale), getBounds().getScaledY((int) (-getRenderAdapter().getFontHeight() * scale) - 2, scale), 0xFFFFFFFF);
        getRenderAdapter().endScale(ctx);
        // Draw border
        getRenderAdapter().drawRect(ctx, getBounds().getX(-1), getBounds().getY(-1), getBounds().getWidth() + 2, getBounds().getHeight() + 2, 0xFF000000);
        // Draw background
        getRenderAdapter().drawRect(ctx, getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds().getHeight(), 0xFF808080);
        // Draw text or placeholder
        String displayText = value.isEmpty() ? placeholder : value;
        getRenderAdapter().drawText(ctx, displayText, getBounds().getX(1), getBounds().getVerticalCenter(getRenderAdapter().getFontHeight()), getTextColor());
        // draw blinking cursor
        if(isFocused())
        {
            long time = System.currentTimeMillis();
            if((time / 500) % 2 == 0)
            {
                int cursorX = getBounds().getX(1 + getRenderAdapter().getTextWidth(displayText.substring(0, cursorPosition)));
                getRenderAdapter().drawRect(ctx, cursorX, getBounds().getY(1), 1, getBounds().getHeight(-2), 0xFFFFFFFF);
            }
        }
    }

    /**
     * Determine the text color based on validation.<br>
     * Gray if empty, red if invalid, white if valid.
     */
    private int getTextColor()
    {
        if(value.isEmpty())
            return 0xFFE0E0E0; // gray for empty input

        if(inputType.getValidator() != null && !inputType.getValidator().test(value))
            return 0xFFFF0000; // red for invalid input

        return 0xFFFFFFFF; // black for valid input
    }

    private boolean isFocused()
    {
        return isFocused;
    }

    @Getter
    @AllArgsConstructor
    public enum TextInputType
    {
        STRING((value) -> true),
        INTEGER(TextInputType::isInteger),
        FLOAT(TextInputType::isFloat);

        private final Predicate<String> validator;

        static boolean isInteger(String value)
        {
            try
            {
                Integer.parseInt(value);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        static boolean isFloat(String value)
        {
            try
            {
                Float.parseFloat(value);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }
    }
}