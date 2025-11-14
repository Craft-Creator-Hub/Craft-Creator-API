package fr.en0ri4n.craftcreator.api.ui;

import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical screen made of core UI elements.
 */
@Getter
public class CoreScreenDefinition {

    private final String id;
    private final String title;
    private final List<CoreUiElement> elements = new ArrayList<>();

    public CoreScreenDefinition(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public CoreScreenDefinition addElement(CoreUiElement element) {
        elements.add(element);
        return this;
    }

    public CoreUiElement findById(String elementId) {
        return elements.stream()
                .filter(e -> elementId.equals(e.getId()))
                .findFirst()
                .orElse(null);
    }
}