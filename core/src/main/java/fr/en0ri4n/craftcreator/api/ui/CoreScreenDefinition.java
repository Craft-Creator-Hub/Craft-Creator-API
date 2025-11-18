package fr.en0ri4n.craftcreator.api.ui;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
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

    private final ContainerModel parent;
    private final Identifier id;
    private final String title;
    private final List<CoreUiElement> elements = new ArrayList<>();

    public CoreScreenDefinition(ContainerModel parent, Identifier id, String title) {
        this.parent = parent;
        this.id = id;
        this.title = title;
    }

    public void addElement(CoreUiElement element) {
        elements.add(element);
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

    public void renderBackground(RenderContext ctx, int x, int y, int width, int height) {
        RenderAdapter adapter = CraftCreatorAPI.get().getPlatform().getRenderAdapter();
        adapter.bindTexture(ctx, getBackgroundTexture());
        adapter.renderTexture(ctx, getBackgroundTexture(),
                x, y, width, height,
                256, 256,
                0f, 0f, 256f, 256f,
                0f);
    }

    public void fetchData() {
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().fetchData(new FetchData(getParent().getBlockEntityPos(), getId()));
    }

    public abstract void updateScreen(UiUpdateData data);

    public void onButtonPressed(String elementId, String actionId) { /* Default implementation does nothing */ }

    public void onDropdownChanged(String elementId, int index, String value) { /* Default implementation does nothing */ }
}