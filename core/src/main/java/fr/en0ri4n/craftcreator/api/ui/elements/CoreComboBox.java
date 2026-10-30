package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Text input with a dropdown of values: type freely, or open the dropdown with the arrow button (or by typing)
 * and pick a value with the mouse or the arrow keys + Enter. Typing filters the dropdown.
 * <p>
 * The dropdown is drawn in {@link #renderForeground} so it covers the elements below it.
 */
public class CoreComboBox extends CoreUiElement
{
    private static final int BUTTON_WIDTH = 14;
    private static final int ROW_HEIGHT = 12;
    private static final int MAX_POPUP_HEIGHT = 110;
    private static final int KEY_ENTER = 257;
    private static final int KEY_KP_ENTER = 335;
    private static final int KEY_UP = 265;
    private static final int KEY_DOWN = 264;

    @Getter
    private final CoreTextInput input;
    private final CoreList<String> popup;
    private final Core2DBounds buttonBounds;
    private Supplier<List<String>> valuesSupplier;
    private List<String> values;
    private Consumer<String> onChange = value -> {};
    @Getter
    private boolean open;

    public CoreComboBox(String id, int x, int y, int width, int height, String label, String value, String placeholder, String tooltip, Supplier<List<String>> values)
    {
        super(CoreUiElementType.TEXT_INPUT, id, x, y, width, height, tooltip);
        this.valuesSupplier = values;
        this.input = new CoreTextInput(id + "_input", CoreTextInput.TextInputType.STRING, x, y, width - BUTTON_WIDTH - 1, height, label, value, placeholder, 256, tooltip);
        this.buttonBounds = Core2DBounds.of(x + width - BUTTON_WIDTH, y, BUTTON_WIDTH, height);
        this.popup = new CoreList<>(x, y + height + 1, width, ROW_HEIGHT * 3, ROW_HEIGHT, List.of());
        this.popup.setEmptyText("No match");
        this.input.setOnChange(this::onTyped);
    }

    public String getValue()
    {
        return input.getValue();
    }

    public void setValue(String value)
    {
        input.setValue(value);
    }

    public void setOnChange(Consumer<String> onChange)
    {
        this.onChange = onChange == null ? value -> {} : onChange;
    }

    /**
     * Use other values from now on (e.g. they depend on another field).
     */
    public void setValues(Supplier<List<String>> values)
    {
        this.valuesSupplier = values;
        this.values = null;
        if(open) refreshPopup(true);
    }

    /* ---------------- popup ---------------- */

    private void onTyped(String value)
    {
        onChange.accept(value);
        if(input.isFocused())
        {
            if(!open) setOpen(true, true);
            else popup.filterByLabel(value);
        }
    }

    private void setOpen(boolean open, boolean filtered)
    {
        this.open = open;
        if(open) refreshPopup(filtered);
    }

    /**
     * @param filtered only show values containing the typed text, otherwise show everything with the current value selected
     */
    private void refreshPopup(boolean filtered)
    {
        if(values == null) values = valuesSupplier.get();

        popup.setEntries(values.stream().map(v -> new CoreList.Entry<>(v, v, entry -> select(entry.getValue()))).toList());
        popup.filterByLabel(filtered ? getValue() : "");
        if(!filtered) popup.setSelectedValue(getValue());
        layoutPopup();
    }

    /**
     * Below the field, or above it when there isn't enough room under it.
     */
    private void layoutPopup()
    {
        Core2DBounds b = getBounds();
        int height = Math.min(MAX_POPUP_HEIGHT, Math.max(1, popup.getVisibleEntries().size()) * ROW_HEIGHT + 6);
        boolean fitsBelow = b.getBottom() + 1 + height <= getRenderAdapter().getScreenHeight() - 2;

        popup.getBounds().setX(b.getX());
        popup.getBounds().setY(fitsBelow ? b.getBottom() + 1 : b.getY() - 1 - height);
        popup.getBounds().setWidth(b.getWidth());
        popup.getBounds().setHeight(height);
    }

    private void select(String value)
    {
        input.setValue(value);
        open = false;
    }

    /* ---------------- input ---------------- */

    @Override
    public boolean isMouseOverElement(int mouseX, int mouseY)
    {
        if(!isVisible()) return false;
        return getBounds().contains(mouseX, mouseY) || (open && popup.getBounds().contains(mouseX, mouseY));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(!isVisible()) return false;

        if(open && popup.getBounds().contains(mouseX, mouseY))
            return popup.mouseClicked(mouseX, mouseY, button);

        if(buttonBounds.contains(mouseX, mouseY))
        {
            setOpen(!open, false);
            input.mouseClicked(input.getBounds().getX(), input.getBounds().getY(), button); // focus the text too
            return true;
        }

        if(input.mouseClicked(mouseX, mouseY, button)) return true;

        loseFocus();
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(!isVisible() || !input.isFocused()) return false;

        if(open && (keyCode == KEY_UP || keyCode == KEY_DOWN))
            return popup.keyPressed(keyCode, scanCode, modifiers);

        if(open && (keyCode == KEY_ENTER || keyCode == KEY_KP_ENTER))
        {
            popup.getSelected().or(() -> popup.getVisibleEntries().stream().findFirst()).ifPresent(entry -> select(entry.getValue()));
            return true;
        }

        return input.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        return isVisible() && input.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        return open && popup.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int button)
    {
        return open && popup.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        return open && popup.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void loseFocus()
    {
        open = false;
        input.loseFocus();
    }

    /* ---------------- rendering ---------------- */

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        if(!isVisible()) return;

        input.render(ctx, mouseX, mouseY, partialTick);

        boolean hovered = buttonBounds.contains(mouseX, mouseY);
        CoreTheme.drawInset(getRenderAdapter(), ctx, buttonBounds, open);
        if(hovered) getRenderAdapter().drawRect(ctx, buttonBounds.getX(2), buttonBounds.getY(2), buttonBounds.getWidth(-4), buttonBounds.getHeight(-4), CoreTheme.ROW_HOVER);

        CoreTheme.drawArrow(getRenderAdapter(), ctx, buttonBounds.getHorizontalCenter(), buttonBounds.getVerticalCenter(), !open,
                            hovered || open ? CoreTheme.TEXT_BRIGHT : CoreTheme.TEXT_MUTED);
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        if(!isVisible()) return;

        if(open)
        {
            layoutPopup();
            popup.render(ctx, mouseX, mouseY, 0);
            popup.renderForeground(ctx, mouseX, mouseY);
        }
        else
        {
            input.renderForeground(ctx, mouseX, mouseY);
        }
    }
}
