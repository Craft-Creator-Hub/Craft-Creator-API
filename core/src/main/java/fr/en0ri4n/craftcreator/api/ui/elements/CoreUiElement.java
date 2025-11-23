package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    protected final int x;
    protected final int y;

    /** Width/height in logical units (pixels or grid units, up to you). */
    protected final int width;
    protected final int height;

    /** tooltip text. */
    private final String tooltip;

    private CoreElementListener<?> elementListener;

    /** Convenience: auto-generate a random id. */
    protected CoreUiElement(CoreUiElementType type, int x, int y, int width, int height, String tooltip) {
        this(type, UUID.randomUUID().toString(), x, y, width, height, tooltip);
    }

    public void setListener(CoreElementListener<?> listener) {
        elementListener = listener;
    }

    public void removeListener(CoreElementListener<?> listener) {
        elementListener = null;
    }

    public void sendUpdate() {
        if(elementListener != null)
            elementListener.update();
    }

    protected boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public abstract void render(RenderContext ctx, int mouseX, int mouseY);

    protected RenderAdapter getRenderAdapter() {
        return CraftCreatorAPI.get().getPlatform().getRenderAdapter();
    }
}