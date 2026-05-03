package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical screen made of core UI elements.
 */
@Getter
public abstract class CoreScreenDefinition<T extends ScreenData>
{
    public static final Identifier GUI_TEXTURE = Identifier.fromMod("textures/gui/base.png");

    private final Identifier id;
    private final String title;
    private final T screenData;
    private final Core2DBounds size;
    private final List<CoreUiElement> elements = new ArrayList<>();

    public CoreScreenDefinition(Identifier id, String title, T data, Core2DBounds size)
    {
        this.id = id;
        this.title = title;
        this.screenData = data;
        this.size = size;
    }

    public Core2DBounds getGuiSize()
    {
        return Core2DBounds.centerScreen(getSize(), getCurrentRenderAdapter().getScreenWidth(), getCurrentRenderAdapter().getScreenHeight());
    }

    public CoreScreenDefinition(Identifier id, String title, T data)
    {
        this(id, title, data, Core2DBounds.ofSize(176, 166));
    }

    public void addElement(CoreUiElement element)
    {
        elements.add(element);
    }

    public void renderBackground(RenderContext ctx)
    {
        getCurrentRenderAdapter().drawRect(ctx, 0, 0, getCurrentRenderAdapter().getScreenWidth(), getCurrentRenderAdapter().getScreenHeight(), 0xD0101010);
    }

    public void render(RenderContext ctx, int mouseX, int mouseY) { /* Default implementation does nothing */ }

    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        String title = translate(getTitle());
        int x = getGuiSize().getHorizontalCenter(getCurrentRenderAdapter().getTextWidth(title));
        int y = getGuiSize().getY() - 12;
        getCurrentRenderAdapter().drawText(ctx, title, x, y, 0xFFFFFF);
    }

    public Core2DBounds getBackgroundTextureSize()
    {
        return Core2DBounds.ofSize(176, 166);
    }

    /**
     * Initializes the screen. This method needs to be called by the platform-specific screen implementation.<br>
     * You need to add widgets to the {@link CoreScreenDefinition} BEFORE calling this method.
     * @param widgetRenderer The widget renderer to use for adding widgets to the actual screen.
     */
    public final void init(WidgetRenderer widgetRenderer, int screenWidth, int screenHeight)
    {
        cleanScreen();
        initElements(screenWidth, screenHeight);
        getElements().stream()
                .map(element ->
                        getCurrentUiAdapter().createWidget(element))
                .forEach(widgetRenderer::addWidgetToScreen);

        fetchData();
    }

    protected void initElements(int screenWidth, int screenHeight) { /* Default implementation does nothing */ }

    public static void renderTextureWithBounds(RenderContext ctx, Identifier texture, Core2DBounds bounds, boolean isHovered, boolean isDisabled)
    {
        renderTextureWithSize(ctx, texture, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), isHovered, isDisabled);
    }

    /**
     * Renders a scaled background using the default GUI texture.
     */
    public static void renderTextureWithSize(RenderContext ctx, Identifier texture, int left, int top, int width, int height, boolean isHovered, boolean isDisabled)
    {
        RenderAdapter adapter = CraftCreatorAPI.get().getPlatform().getRenderAdapter();

        final int textureWidth = 16;
        final int textureHeight = 48;
        int baseY = isDisabled ? 32 : isHovered ? 16 : 0;

        // Top left
        adapter.drawTexture(ctx, texture, left, top, 5, 5, textureWidth, textureHeight, 0, baseY, 5, 5);
        // Top middle
        adapter.drawTexture(ctx, texture, left + 5, top, width - 10, 5, textureWidth, textureHeight, 5, baseY, 1, 5);
        // Top right
        adapter.drawTexture(ctx, texture, left + width - 5, top, 5, 5, textureWidth, textureHeight, 11, baseY, 5, 5);
        // Middle left
        adapter.drawTexture(ctx, texture, left, top + 5, 5, height - 10, textureWidth, textureHeight, 0, baseY + 5, 5, 1);
        // Middle
        adapter.drawTexture(ctx, texture, left + 5, top + 5, width - 10, height - 10, textureWidth, textureHeight, 5, baseY + 5, 1, 1);
        // Middle right
        adapter.drawTexture(ctx, texture, left + width - 5, top + 5, 5, height - 10, textureWidth, textureHeight, 11, baseY + 5, 5, 1);
        // Bottom left
        adapter.drawTexture(ctx, texture, left, top + height - 5, 5, 5, textureWidth, textureHeight, 0, baseY + 11, 5, 5);
        // Bottom middle
        adapter.drawTexture(ctx, texture, left + 5, top + height - 5, width - 10, 5, textureWidth, textureHeight, 5, baseY + 11, 1, 5);
        // Bottom right
        adapter.drawTexture(ctx, texture, left + width - 5, top + height - 5, 5, 5, textureWidth, textureHeight, 11, baseY + 11, 5, 5);
    }

    protected static String translate(String key, Object... args)
    {
        return CraftCreatorAPI.translate(key, args);
    }

    protected static RenderAdapter getCurrentRenderAdapter()
    {
        return CraftCreatorAPI.get().getPlatform().getRenderAdapter();
    }

    protected static UiAdapter<?> getCurrentUiAdapter()
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

    protected void cleanScreen()
    {
        this.elements.clear();
    }

    public boolean onClick(double mouseX, double mouseY, int button)
    {
        return false;
    }

    public void onClose()
    {
        sendUpdates();
        cleanScreen();
    }
}