package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import lombok.Getter;
import lombok.Setter;

/**
 * Core representation of a single-line text input.
 */
@Getter
public class CoreTextInput extends CoreUiElement {

    @Setter
    private String value;

    private final String placeholder;

    private final int maxLength;

    public CoreTextInput(String id, int x, int y, int width, int height,
                         String value, String placeholder, int maxLength, String tooltip) {
        super(CoreUiElementType.TEXT_INPUT, id, x, y, width, height, tooltip);
        this.value = value;
        this.placeholder = placeholder;
        this.maxLength = maxLength;
    }

    public CoreTextInput(int x, int y, int width, int height,
                         String placeholder, int maxLength) {
        this(null, x, y, width, height, "", placeholder, maxLength, null);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {

    }
}