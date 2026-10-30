package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Loader-agnostic description of a UI element.
 */
@Getter
@RequiredArgsConstructor
public abstract class CoreUiElement {

    private final CoreUiElementType type;

    /** Unique id within the screen, used to wire actions / read state. */
    private final String id;

    /** X/Y position in logical screen coordinates. */
    protected final Core2DBounds bounds;

    /** tooltip text. */
    private final String tooltip;

    /** Whether the element is currently visible. */
    @Setter
    private boolean isVisible = true;

    /** Convenience: auto-generate a random id. */
    protected CoreUiElement(CoreUiElementType type, int x, int y, int width, int height, String tooltip) {
        this(type, UUID.randomUUID().toString(), Core2DBounds.of(x, y, width, height), tooltip);
    }

    public CoreUiElement(CoreUiElementType type, String id, int x, int y, int width, int height, String tooltip)
    {
        this(type, id, Core2DBounds.of(x, y, width, height), tooltip);
    }

    protected boolean isMouseOver(int mouseX, int mouseY)
    {
        if(!isVisible) return false;
        return getBounds().contains(mouseX, mouseY);
    }

    public abstract void render(RenderContext ctx, int mouseX, int mouseY, float partialTick);

    /**
     * Render additional foreground elements, such as tooltips.<br>
     * Need to be called by {@link fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition#renderForeground(RenderContext, int, int)}, of the parent screen.
     */
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    { /* Not implemented by default */ }

    /**
     * Draw this element's tooltip text, if it has one.
     */
    protected void renderTooltip(RenderContext ctx, int mouseX, int mouseY)
    {
        if(tooltip != null && !tooltip.isEmpty())
            CoreTooltip.of(tooltip).render(ctx, mouseX, mouseY);
    }

    protected static RenderAdapter getRenderAdapter() {
        return CraftCreatorAPI.get().getPlatform().getRenderAdapter();
    }

    protected UiAdapter<?> getUiAdapter()
    {
        return CraftCreatorAPI.get().getPlatform().getUiAdapter();
    }

    public abstract boolean mouseClicked(int mouseX, int mouseY, int button);

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }

    public boolean charTyped(char codePoint, int modifiers) { return false; }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) { return false; }

    public boolean mouseDragged(int mouseX, int mouseY, int button) { return false; }

    public boolean mouseReleased(int mouseX, int mouseY, int button) { return false; }

    /**
     * Called when the player clicks somewhere else, e.g. so a text input stops receiving keys.
     */
    public void loseFocus() { /* Nothing to do by default */ }

    /**
     * Used by platforms to route events (e.g. mouse wheel) to the element under the mouse.
     */
    public boolean isMouseOverElement(int mouseX, int mouseY)
    {
        return isMouseOver(mouseX, mouseY);
    }
}