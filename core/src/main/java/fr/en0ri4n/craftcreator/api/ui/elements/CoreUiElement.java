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
    protected final CoreBounds bounds;

    /** tooltip text. */
    private final String tooltip;

    private CoreElementListener<?> elementListener;

    /** Convenience: auto-generate a random id. */
    protected CoreUiElement(CoreUiElementType type, int x, int y, int width, int height, String tooltip) {
        this(type, UUID.randomUUID().toString(), CoreBounds.of(x, y, width, height), tooltip);
    }

    public CoreUiElement(CoreUiElementType type, String id, int x, int y, int width, int height, String tooltip)
    {
        this(type, id, CoreBounds.of(x, y, width, height), tooltip);
    }

    protected boolean isMouseOver(int mouseX, int mouseY) {
        return getBounds().contains(mouseX, mouseY);
    }

    public abstract void render(RenderContext ctx, int mouseX, int mouseY, float partialTick);

    protected RenderAdapter getRenderAdapter() {
        return CraftCreatorAPI.get().getPlatform().getRenderAdapter();
    }

    public abstract boolean mouseClicked(int mouseX, int mouseY, int button);

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }

    public boolean charTyped(char codePoint, int modifiers) { return false; }
}