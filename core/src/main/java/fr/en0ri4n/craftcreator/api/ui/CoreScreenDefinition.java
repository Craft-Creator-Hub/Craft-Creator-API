package fr.en0ri4n.craftcreator.api.ui;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical screen made of core UI elements.
 */
@Getter
public abstract class CoreScreenDefinition {

    private final String id;
    private final String title;
    private final List<CoreUiElement> elements = new ArrayList<>();

    public CoreScreenDefinition(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void addElement(CoreUiElement element) {
        elements.add(element);
    }

    public abstract Identifier getBackgroundTexture();

    public abstract Pair<Integer, Integer> getBackgroundTextureSize();

    public CoreUiElement findById(String elementId) {
        return elements.stream()
                .filter(e -> elementId.equals(e.getId()))
                .findFirst()
                .orElse(null);
    }

    public abstract void init();

    public void renderBackground(RenderContext ctx, int x, int y, int width, int height) {
        RenderAdapter adapter = CraftCreatorAPI.getInstance().getPlatform().getRenderAdapter();
        adapter.bindTexture(ctx, getBackgroundTexture());
        adapter.renderTexture(ctx, getBackgroundTexture(),
                x, y, width, height,
                256, 256,
                0f, 0f, 256f, 256f,
                0f);
    }

    public void onButtonPressed(String elementId, String actionId) { /* Default implementation does nothing */ }

    public void onDropdownChanged(String elementId, int index, String value) { /* Default implementation does nothing */ }
}