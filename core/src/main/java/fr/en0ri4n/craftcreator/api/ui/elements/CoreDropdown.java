package fr.en0ri4n.craftcreator.api.ui.elements;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Core representation of a dropdown (combo box).
 */
@Getter
public class CoreDropdown extends CoreUiElement {

    private final List<String> options;

    /** Index into options; -1 means "none selected". */
    @Setter
    private int selectedIndex;

    public CoreDropdown(String id, int x, int y, int width, int height,
                        List<String> options, int selectedIndex, String tooltip) {
        super(CoreUiElementType.DROPDOWN, id, x, y, width, height, tooltip);
        this.options = List.copyOf(options);
        this.selectedIndex = selectedIndex;
    }

    public CoreDropdown(int x, int y, int width, int height,
                        List<String> options) {
        this(null, x, y, width, height, options, -1, null);
    }

    public String getSelectedValue() {
        if (selectedIndex < 0 || selectedIndex >= options.size()) return null;
        return options.get(selectedIndex);
    }
}