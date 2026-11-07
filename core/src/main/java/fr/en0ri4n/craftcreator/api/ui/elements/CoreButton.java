package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.Getter;
import lombok.Setter;

/**
 * Core representation of a button.
 */
@Getter
public class CoreButton extends CoreUiElement
{
    @Setter
    private String label;

    /**
     * Logical action id, the platform will map this to actual click handlers.
     */
    private final Runnable onPress;

    @Setter
    private boolean enabled = true;

    /** A selected button (e.g. the current tab) is drawn highlighted and can't be clicked. */
    @Setter
    private boolean selected = false;

    public CoreButton(String id, int x, int y, int width, int height, String label, Runnable onPress, String tooltip)
    {
        super(CoreUiElementType.BUTTON, id, x, y, width, height, tooltip);
        this.label = label;
        this.onPress = onPress;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if(!isMouseOver(mouseX, mouseY) || !enabled || selected) return false;

        getUiAdapter().playSound(UiAdapter.UiSound.BUTTON_CLICK);

        if(onPress != null)
            onPress.run();

        return true;
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        if(!isVisible()) return;

        boolean hovered = enabled && !selected && isMouseOver(mouseX, mouseY);
        CoreButtonStyle.State state = CoreButtonStyle.State.of(enabled, selected, hovered);
        CoreButtonStyle.draw(ctx, getBounds(), state);

        int color = selected ? CoreTheme.ACCENT : !enabled ? CoreTheme.TEXT_DISABLED : hovered ? CoreTheme.TEXT_BRIGHT : CoreTheme.TEXT;
        getRenderAdapter().drawText(ctx, label, getBounds().getHorizontalCenter(getRenderAdapter().getTextWidth(label)),
                                    CoreButtonStyle.textY(getBounds(), getRenderAdapter().getFontHeight(), state), color);
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        if(isMouseOver(mouseX, mouseY))
            renderTooltip(ctx, mouseX, mouseY);
    }
}