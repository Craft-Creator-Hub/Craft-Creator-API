package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Core representation of a single-line text input.
 */
@Getter
public class CoreTextInput extends CoreUiElement
{
    private static final int TEXT_PADDING = 4;

    private String value;
    @Setter
    private Consumer<String> onChange = value -> {};

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

    /**
     * Replace the text and move the cursor to its end.
     */
    public void setValue(String value)
    {
        updateValue(value);
        this.cursorPosition = this.value.length();
    }

    private void updateValue(String value)
    {
        this.value = value == null ? "" : value;
        this.cursorPosition = Math.min(cursorPosition, this.value.length());
        onChange.accept(this.value);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(isFocused() && keyCode != 256) // let escape close the screen
        {
            if(keyCode == 259) // backspace
            {
                if(cursorPosition > 0 && !value.isEmpty())
                {
                    cursorPosition--;
                    updateValue(value.substring(0, cursorPosition) + value.substring(cursorPosition + 1));
                }
            }
            else if(keyCode == 261) // delete
            {
                if(cursorPosition < value.length())
                    updateValue(value.substring(0, cursorPosition) + value.substring(cursorPosition + 1));
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
                cursorPosition++;
                updateValue(newValue);
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
        if(!isVisible()) return;

        Core2DBounds b = getBounds();
        if(label != null && !label.isEmpty())
        {
            // Small label above the field
            float scale = 6F / 9F;
            getRenderAdapter().startScale(ctx, scale, scale);
            getRenderAdapter().drawText(ctx, label, b.getScaledX(scale), b.getScaledY((int) (-getRenderAdapter().getFontHeight() * scale) - 2, scale), CoreTheme.TEXT_MUTED);
            getRenderAdapter().endScale(ctx);
        }

        CoreTheme.drawInset(getRenderAdapter(), ctx, b, isFocused);

        int textX = b.getX(TEXT_PADDING);
        int textY = b.getVerticalCenter(getRenderAdapter().getFontHeight()) + 1;
        int visibleWidth = b.getWidth() - TEXT_PADDING * 2;

        getRenderAdapter().enableScissor(ctx, textX, b.getY(1), visibleWidth, b.getHeight(-2));
        if(value.isEmpty())
        {
            if(!isFocused) getRenderAdapter().drawText(ctx, placeholder, textX, textY, CoreTheme.TEXT_DISABLED);
        }
        else
        {
            // Shift the text left so the cursor stays visible in long values
            int cursorOffset = getRenderAdapter().getTextWidth(value.substring(0, cursorPosition));
            int shift = Math.max(0, cursorOffset - visibleWidth + 2);
            getRenderAdapter().drawText(ctx, value, textX - shift, textY, getTextColor());
        }

        if(isFocused && (System.currentTimeMillis() / 500) % 2 == 0)
        {
            int cursorOffset = getRenderAdapter().getTextWidth(value.substring(0, cursorPosition));
            int cursorX = textX + Math.min(cursorOffset, visibleWidth - 2);
            getRenderAdapter().drawRect(ctx, cursorX, b.getY(3), 1, b.getHeight(-6), CoreTheme.TEXT_BRIGHT);
        }
        getRenderAdapter().disableScissor(ctx);
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        if(isMouseOver(mouseX, mouseY) && !isFocused)
            renderTooltip(ctx, mouseX, mouseY);
    }

    /**
     * Red when the value doesn't match the input type (e.g. letters in a number field).
     */
    private int getTextColor()
    {
        if(inputType.getValidator() != null && !inputType.getValidator().test(value))
            return CoreTheme.TEXT_ERROR;
        return CoreTheme.TEXT;
    }

    public boolean isFocused()
    {
        return isFocused;
    }

    @Override
    public void loseFocus()
    {
        isFocused = false;
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