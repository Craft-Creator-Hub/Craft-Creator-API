package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import lombok.Getter;

import java.util.List;

/**
 * Core representation of a dropdown (combo box).
 */
@Getter
public class CoreDropdown<T> extends CoreUiElement
{
    private final List<T> options;

    /**
     * Index into options; -1 means "none selected".
     */
    private int selectedIndex;
    private final Runnable onChange;

    public CoreDropdown(String id, int x, int y, int width, int height, List<T> options, int selectedIndex, String tooltip, Runnable onChange)
    {
        super(CoreUiElementType.DROPDOWN, id, x, y, width, height, tooltip);
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
            onChange.run();
        sendUpdate();
    }

    public void setSelectedValue(T value)
    {
        int index = options.indexOf(value);
        setSelectedIndex(index);
    }

    public void onClick(int mouseX, int mouseY)
    {
        if(!isMouseOver(mouseX, mouseY)) return;

        int size = getOptions().size();
        if(size == 0) return;

        int current = getSelectedIndex();
        int next = (current + 1) % size;
        setSelectedIndex(next);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        int actualWidth = Math.max(this.width, getRenderAdapter().getTextWidth(this.getSelectedValueAsString()) + 10);

        int bgColor = this.isMouseOver(mouseX, mouseY) ? 0xFF777777 : 0xFF555555;
        CoreScreenDefinition.renderTextureWithSize(ctx, CoreScreenDefinition.GUI_TEXTURE, this.x, this.y, actualWidth, this.height, this.isMouseOver(mouseX, mouseY), false);

        // Draw label centered
        int color = 0xFFFFFFFF;
        int textX = this.x + (actualWidth - getRenderAdapter().getTextWidth(this.getSelectedValueAsString())) / 2;
        int textY = this.y + (this.height - getRenderAdapter().getFontHeight()) / 2;
        getRenderAdapter().drawText(ctx, getSelectedValueAsString(), textX, textY, color);
    }
}