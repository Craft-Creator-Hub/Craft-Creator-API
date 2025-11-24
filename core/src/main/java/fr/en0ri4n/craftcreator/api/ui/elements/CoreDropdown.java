package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;

/**
 * Core representation of a dropdown (combo box).
 */
@Getter
public class CoreDropdown<T> extends CoreUiElement
{
    private final boolean hasFixedWidth;
    private final List<T> options;

    /**
     * Index into options; -1 means "none selected".
     */
    private int selectedIndex;
    private final Consumer<T> onChange;

    public CoreDropdown(String id, int x, int y, int width, int height, boolean hasFixedWidth, List<T> options, int selectedIndex, String tooltip, Consumer<T> onChange)
    {
        super(CoreUiElementType.DROPDOWN, id, x, y, width, height, tooltip);
        this.hasFixedWidth = hasFixedWidth;
        this.options = List.copyOf(options);
        this.selectedIndex = selectedIndex;
        this.onChange = onChange;
    }

    public T getSelectedValue()
    {
        if(selectedIndex < 0 || selectedIndex >= options.size()) return null;
        return options.get(selectedIndex);
    }

    public String getSelectedValueAsString()
    {
        T value = getSelectedValue();
        return value != null ? value.toString() : "<none>";
    }

    public void setSelectedIndex(int selectedIndex)
    {
        this.selectedIndex = selectedIndex;
        if(onChange != null)
            onChange.accept(getSelectedValue());
        calculateNewBounds();
    }

    private void calculateNewBounds()
    {
        if(hasFixedWidth) return;
        int actualWidth = getRenderAdapter().getTextWidth(this.getSelectedValueAsString()) + 10;
        getBounds().setWidth(actualWidth);
        // Adjust x
        getBounds().setX(getBounds().getX() - (actualWidth - getBounds().getWidth()) / 2);

    }

    public void setSelectedValue(T value)
    {
        int index = options.indexOf(value);
        setSelectedIndex(index);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(!isMouseOver(mouseX, mouseY)) return false;

        int size = getOptions().size();
        if(size == 0) return false;

        int current = getSelectedIndex();
        int next = (current + 1) % size;
        setSelectedIndex(next);

        return true;
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float pPartialTick)
    {
        int actualWidth = Math.max(getBounds().getWidth(), getRenderAdapter().getTextWidth(this.getSelectedValueAsString()) + 10);

        CoreScreenDefinition.renderTextureWithSize(ctx, CoreScreenDefinition.GUI_TEXTURE,
                getBounds().getX(), getBounds().getY(),
                actualWidth, getBounds().getHeight(),
                isMouseOver(mouseX, mouseY), false);

        // Draw label centered
        int color = 0xFFFFFFFF;
        int textX = getBounds().getX((actualWidth - getRenderAdapter().getTextWidth(this.getSelectedValueAsString())) / 2);
        int textY = getBounds().getVerticalCenter(getRenderAdapter().getFontHeight());
        getRenderAdapter().drawText(ctx, getSelectedValueAsString(), textX, textY, color);
    }
}