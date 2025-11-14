package fr.en0ri4n.craftcreator.api.ui.elements;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Core representation of a scrollable list of strings.
 * For more complex entries you could replace String with a small data class.
 */
@Getter
public class CoreList extends CoreUiElement {

    private final List<String> entries;

    @Setter
    private int selectedIndex = -1;

    /** How many rows are visible at once. */
    private final int visibleRows;

    public CoreList(String id, int x, int y, int width, int height,
                    List<String> entries, int visibleRows, String tooltip) {
        super(id, x, y, width, height, tooltip);
        this.entries = List.copyOf(entries);
        this.visibleRows = visibleRows;
    }

    public CoreList(int x, int y, int width, int height,
                    List<String> entries, int visibleRows) {
        this(null, x, y, width, height, entries, visibleRows, null);
    }

    public String getSelectedValue() {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) return null;
        return entries.get(selectedIndex);
    }
}