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
public abstract class CoreScreenDefinition<T extends ScreenData> {

    private final Identifier id;
    private final String title;
    private final T screenData;
    private final List<CoreUiElement> elements = new ArrayList<>();
    private final List<CoreElementListener<?>> elementListeners = new ArrayList<>();

    public CoreScreenDefinition(Identifier id, String title, T data) {
        this.id = id;
        this.title = title;
        this.screenData = data;
    }

    public void addElement(CoreUiElement element) {
        elements.add(element);
    }

    public void addElementListener(CoreElementListener<?> listener) {
        elementListeners.add(listener);
    }

    public void sendUpdate(CoreUiElement element) {
        for(CoreElementListener<?> listener : elementListeners) {
            if(listener.getElement().equals(element)) {
                listener.update();
                break;
            }
        }
    }

    public abstract Identifier getBackgroundTexture();

    public Pair<Integer, Integer> getBackgroundTextureSize()
    {
        return Pair.create(176, 166);
    }

    public CoreUiElement findById(String elementId) {
        return elements.stream()
                .filter(e -> elementId.equals(e.getId()))
                .findFirst()
                .orElse(null);
    }

    public abstract void init();

    public void initScreen() {
        if(!elements.isEmpty()) {
            elements.clear();
        }
        init();
    }

    public void renderBackground(RenderContext ctx, int x, int y, int width, int height) {
        RenderAdapter adapter = CraftCreatorAPI.get().getPlatform().getRenderAdapter();
        adapter.bindTexture(ctx, getBackgroundTexture());
        adapter.renderTexture(ctx, getBackgroundTexture(),
                x, y, width, height,
                256, 256,
                0f, 0f, 256f, 256f,
                0f);
    }

    public void onButtonPressed(String elementId, String actionId) { /* Default implementation does nothing */ }

    public void onDropdownChanged(String elementId, int index, String value) { /* Default implementation does nothing */ }

    public void onClose()
    {
        this.elements.clear();
        this.elementListeners.clear();
    }
}