package fr.en0ri4n.craftcreator.api.ui.elements;

import lombok.Getter;
import lombok.Setter;

/**
 * Core representation of a button.
 */
@Getter
public class CoreButton extends CoreUiElement {

    private final String label;

    /** Logical action id, the platform will map this to actual click handlers. */
    private final String actionId;

    @Setter
    private boolean enabled = true;

    public CoreButton(String id, int x, int y, int width, int height,
                      String label, String actionId, String tooltip) {
        super(id, x, y, width, height, tooltip);
        this.label = label;
        this.actionId = actionId;
    }

    public CoreButton(int x, int y, int width, int height,
                      String label, String actionId) {
        this(null, x, y, width, height, label, actionId, null);
    }
}