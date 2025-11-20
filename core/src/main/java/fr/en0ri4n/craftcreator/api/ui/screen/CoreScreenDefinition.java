package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
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
public abstract class CoreScreenDefinition<T extends ScreenData>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromMod("textures/gui/base.png");

    private final Identifier id;
    private final String title;
    private final T screenData;
    private final Pair<Integer, Integer> guiSize;
    private final List<CoreUiElement> elements = new ArrayList<>();

    public CoreScreenDefinition(Identifier id, String title, T data, Pair<Integer, Integer> guiSize)
    {
        this.id = id;
        this.title = title;
        this.screenData = data;
        this.guiSize = guiSize;
    }

    public CoreScreenDefinition(Identifier id, String title, T data)
    {
        this(id, title, data, Pair.of(176, 166));
    }

    protected int getGuiLeft()
    {
        int screenWidth = getCurrentRenderAdapter().getScreenWidth();
        int guiWidth = getGuiSize().getFirst();
        return (screenWidth - guiWidth) / 2;
    }

    protected int getGuiTop()
    {
        int screenHeight = getCurrentRenderAdapter().getScreenHeight();
        int guiHeight = getGuiSize().getSecond();
        return (screenHeight - guiHeight) / 2;
    }

    public void addElement(CoreUiElement element)
    {
        elements.add(element);
    }

    public abstract void renderBackground(RenderContext ctx);

    public void render(RenderContext ctx) { /* Default implementation does nothing */ }

    public Pair<Integer, Integer> getBackgroundTextureSize()
    {
        return Pair.of(176, 166);
    }

    /**
     * Initializes the screen. This method needs to be called by the platform-specific screen implementation.<br>
     * You need to add widgets to the {@link CoreScreenDefinition} BEFORE calling this method.
     * @param widgetRenderer The widget renderer to use for adding widgets to the screen.
     */
    public void init(WidgetRenderer widgetRenderer)
    {
        getElements().stream()
                .map(element ->
                        getCurrentUiAdapter().createWidget(element, getGuiLeft(), getGuiTop(), this))
                .forEach(widgetRenderer::addWidgetToScreen);

        fetchData();
    }

    /**
     * Renders a scaled background using the default GUI texture.
     */
    protected void renderBackgroundWithSize(RenderContext ctx, int width, int height)
    {
        RenderAdapter adapter = getCurrentRenderAdapter();
        int left = (adapter.getScreenWidth() - width) / 2;
        int top = (adapter.getScreenHeight() - height) / 2;
        adapter.bindTexture(ctx, GUI_TEXTURE);
        // Top left
        adapter.drawTexture(ctx, GUI_TEXTURE, left, top, 5, 5, 16, 16, 0, 0, 5, 5);
        // Top middle
        adapter.drawTexture(ctx, GUI_TEXTURE, left + 5, top, width - 10, 5, 16, 16, 5, 0, 1, 5);
        // Top right
        adapter.drawTexture(ctx, GUI_TEXTURE, left + width - 5, top, 5, 5, 16, 16, 11, 0, 5, 5);
        // Middle left
        adapter.drawTexture(ctx, GUI_TEXTURE, left, top + 5, 5, height - 10, 16, 16, 0, 5, 5, 1);
        // Middle
        adapter.drawTexture(ctx, GUI_TEXTURE, left + 5, top + 5, width - 10, height - 10, 16, 16, 5, 5, 1, 1);
        // Middle right
        adapter.drawTexture(ctx, GUI_TEXTURE, left + width - 5, top + 5, 5, height - 10, 16, 16, 11, 5, 5, 1);
        // Bottom left
        adapter.drawTexture(ctx, GUI_TEXTURE, left, top + height - 5, 5, 5, 16, 16, 0, 11, 5, 5);
        // Bottom middle
        adapter.drawTexture(ctx, GUI_TEXTURE, left + 5, top + height - 5, width - 10, 5, 16, 16, 5, 11, 1, 5);
        // Bottom right
        adapter.drawTexture(ctx, GUI_TEXTURE, left + width - 5, top + height - 5, 5, 5, 16, 16, 11, 11, 5, 5);
    }

    public RenderAdapter getCurrentRenderAdapter()
    {
        return CraftCreatorAPI.get().getPlatform().getRenderAdapter();
    }

    public UiAdapter<?> getCurrentUiAdapter()
    {
        return CraftCreatorAPI.get().getPlatform().getUiAdapter();
    }

    public void fetchData()
    { /* Default implementation does nothing */ }

    public void sendUpdates()
    { /* Default implementation does nothing */ }

    public void updateScreen(UiUpdateData data)
    {
        getScreenData().load(data.getPayload());
    }

    public void onButtonPressed(String elementId, String actionId)
    { /* Default implementation does nothing */ }

    public void onDropdownChanged(String elementId, int index, String value)
    { /* Default implementation does nothing */ }

    // Text inputs
    public void onTextChanged(String elementId, String value)
    { /* Default implementation does nothing */ }

    public boolean onClick(double mouseX, double mouseY, int button)
    {
        return false;
    }

    public void onClose()
    {
        this.elements.clear();
        sendUpdates();
    }
}