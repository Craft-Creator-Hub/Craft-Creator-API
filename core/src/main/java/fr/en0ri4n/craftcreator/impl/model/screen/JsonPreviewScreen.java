package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreJsonView;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreNotifications;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTheme;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.utils.Identifier;

/**
 * The json of the recipe being made, without exporting it: scrollable, with a button copying it to the clipboard
 * and one going back to the recipe creator.
 */
public class JsonPreviewScreen extends CoreScreenDefinition<JsonPreviewScreen.NoData>
{
    private static final int WIDTH = 300;
    private static final int HEIGHT = 210;
    private static final int MARGIN = 8;
    private static final int BUTTON_HEIGHT = 20;

    private final RecipeCreatorContainerScreenDefinition<?> parent;
    private final String json;
    private final String recipeName;

    public JsonPreviewScreen(RecipeCreatorContainerScreenDefinition<?> parent, String json, String recipeName)
    {
        super(Identifier.fromMod("json_preview_screen"), "screen.json_preview.title", new NoData(), Core2DBounds.ofSize(WIDTH, HEIGHT));
        this.parent = parent;
        this.json = json;
        this.recipeName = recipeName;
    }

    @Override
    protected void initElements(int screenWidth, int screenHeight)
    {
        Core2DBounds gui = getGuiSize();
        int buttonsY = gui.getBottom(-MARGIN - BUTTON_HEIGHT);
        addElement(new CoreJsonView("json", gui.getX(MARGIN), gui.getY(MARGIN + 12), gui.getWidth() - MARGIN * 2, buttonsY - gui.getY(MARGIN + 12) - 6, json));
        addElement(new CoreButton("back", gui.getX(MARGIN), buttonsY, 60, BUTTON_HEIGHT, "Back", this::goBack, "Return to the recipe creator"));
        addElement(new CoreButton("copy", gui.getRight(-MARGIN - 80), buttonsY, 80, BUTTON_HEIGHT, "Copy JSON", this::copy, "Copy the recipe's json to the clipboard"));
    }

    @Override
    public void renderBackground(RenderContext ctx)
    {
        super.renderBackground(ctx);
        renderTextureWithBounds(ctx, GUI_TEXTURE, getGuiSize(), false, false);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        // The recipe's file name, shown above the json
        String label = recipeName == null ? "" : recipeName + ".json";
        getRenderAdapter().drawText(ctx, label, getGuiSize().getX(MARGIN), getGuiSize().getY(MARGIN), CoreTheme.TEXT_MUTED);
    }

    private void copy()
    {
        getCurrentUiAdapter().setClipboard(json);
        notify("JSON copied to the clipboard", CoreNotifications.Kind.SUCCESS);
    }

    /**
     * Reopen the recipe creator: its container is opened again by the server, like after picking a tag.
     */
    private void goBack()
    {
        OpenContainerRequestData request = new OpenContainerRequestData(parent.getParentContainerModel().getBlockEntityPos(), parent.getId());
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendOpenContainerRequestToServer(request);
    }

    /**
     * This screen only shows what it was given.
     */
    public static class NoData implements ScreenData
    {
        @Override
        public void load(JsonObject jsonObject) { /* Nothing to load */ }

        @Override
        public JsonObject save()
        {
            return new JsonObject();
        }
    }
}
