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

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(!isMouseOver(mouseX, mouseY) || !enabled) return false;

        if(onPress != null)
            onPress.run();

        return true;
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        CoreScreenDefinition.renderTextureWithSize(ctx, CoreScreenDefinition.GUI_TEXTURE, getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds().getHeight(), isMouseOver(mouseX, mouseY), !enabled);
        getRenderAdapter().drawText(ctx, label, getBounds().getHorizontalCenter(getRenderAdapter().getTextWidth(label)), getBounds().getVerticalCenter(getRenderAdapter().getFontHeight()), enabled ? 0xFFFFFF : 0xAAAAAA);
    }
}