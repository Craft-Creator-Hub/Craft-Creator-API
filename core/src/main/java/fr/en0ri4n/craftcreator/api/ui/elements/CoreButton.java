package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import lombok.Getter;
import lombok.Setter;

/**
 * Core representation of a button.
 */
@Getter
public class CoreButton extends CoreUiElement
{

    private final String label;

    /**
     * Logical action id, the platform will map this to actual click handlers.
     */
    private final Runnable onPress;

    @Setter
    private boolean enabled = true;

    public CoreButton(String id, int x, int y, int width, int height, String label, Runnable onPress, String tooltip)
    {
        super(CoreUiElementType.BUTTON, id, x, y, width, height, tooltip);
        this.label = label;
        this.onPress = onPress;
    }

    public void onClick(int pMouseX, int pMouseY)
    {
        if(!isMouseOver(pMouseX, pMouseY) || !enabled) return;

        if(onPress != null)
            onPress.run();
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        CoreScreenDefinition.renderTextureWithSize(ctx, CoreScreenDefinition.GUI_TEXTURE, getX(), getY(), getWidth(), getHeight(), isMouseOver(mouseX, mouseY), !enabled);
        getRenderAdapter().drawText(ctx, label, getX() + (getWidth() - getRenderAdapter().getTextWidth(label)) / 2, getY() + (getHeight() - getRenderAdapter().getFontHeight()) / 2, enabled ? 0xFFFFFF : 0xAAAAAA);
    }
}