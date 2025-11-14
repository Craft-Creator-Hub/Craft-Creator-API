package fr.en0ri4n.craftcreator.api.ui.elements;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Loader-agnostic description of a UI element.
 * No Minecraft/loader classes here.
 */
@Getter
@RequiredArgsConstructor
public abstract class CoreUiElement {

    /** Unique id within the screen, used to wire actions / read state. */
    private final String id;

    /** X/Y position in logical screen coordinates. */
    private final int x;
    private final int y;

    /** Width/height in logical units (pixels or grid units, up to you). */
    private final int width;
    private final int height;

    /** Optional tooltip text. */
    private final String tooltip;

    /** Convenience: auto-generate a random id. */
    protected CoreUiElement(int x, int y, int width, int height, String tooltip) {
        this(UUID.randomUUID().toString(), x, y, width, height, tooltip);
    }
}