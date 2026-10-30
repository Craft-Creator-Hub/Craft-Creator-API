package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTheme;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * A few pages explaining how to use the mod. Page texts are in the lang file:
 * "screen.tutorial.page_{n}.title" and "screen.tutorial.page_{n}.text" (lines separated by \n).
 */
public class TutorialScreen extends CoreScreenDefinition<TutorialScreen.TutorialScreenData>
{
    private static final int PAGE_COUNT = 4;
    private static final int TEXT_PADDING = 12;

    private final CoreScreenDefinition<?> parent;
    private int page;
    private CoreButton previousButton;
    private CoreButton nextButton;

    public TutorialScreen(CoreScreenDefinition<?> parent)
    {
        super(Identifier.fromMod("tutorial_screen"), "screen.tutorial.title", new TutorialScreenData(), Core2DBounds.ofSize(300, 180));
        this.parent = parent;
    }

    @Override
    protected void initElements(int screenWidth, int screenHeight)
    {
        Core2DBounds gui = getGuiSize();
        int y = gui.getBottom(-28);

        addElement(previousButton = new CoreButton("previous", gui.getX(10), y, 20, 18, translateToString("screen.recipe_creator.button.previous"), () -> setPage(page - 1), null));
        addElement(new CoreButton("back", gui.getHorizontalCenter(60), y, 60, 18, translateToString("screen.button.back"), this::close, null));
        addElement(nextButton = new CoreButton("next", gui.getRight(-30), y, 20, 18, translateToString("screen.recipe_creator.button.next"), () -> setPage(page + 1), null));
        setPage(page);
    }

    private void setPage(int newPage)
    {
        page = Math.max(0, Math.min(PAGE_COUNT - 1, newPage));
        previousButton.setVisible(page > 0);
        nextButton.setVisible(page < PAGE_COUNT - 1);
    }

    private void close()
    {
        if(parent != null) getCurrentUiAdapter().openScreen(parent);
        else getCurrentUiAdapter().closeScreen();
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
        Core2DBounds gui = getGuiSize();
        int maxWidth = gui.getWidth() - TEXT_PADDING * 2;
        int y = gui.getY(TEXT_PADDING);

        String pageTitle = translateToString("screen.tutorial.page_" + page + ".title");
        getRenderAdapter().drawText(ctx, pageTitle, gui.getHorizontalCenter(getRenderAdapter().getTextWidth(pageTitle)), y, CoreTheme.TEXT_TITLE);
        y += getRenderAdapter().getFontHeight() + 8;

        for(String paragraph : translateToString("screen.tutorial.page_" + page + ".text").split("\n"))
        {
            for(String line : wrap(paragraph, maxWidth))
            {
                getRenderAdapter().drawText(ctx, line, gui.getX(TEXT_PADDING), y, CoreTheme.TEXT);
                y += getRenderAdapter().getFontHeight() + 2;
            }
        }

        String counter = translateToString("screen.tutorial.page", page + 1, PAGE_COUNT);
        getRenderAdapter().drawText(ctx, counter, gui.getRight(-TEXT_PADDING - getRenderAdapter().getTextWidth(counter)), gui.getY(TEXT_PADDING), CoreTheme.TEXT_MUTED);
    }

    /**
     * Split a paragraph into lines fitting the given width, breaking between words.
     */
    private static List<String> wrap(String paragraph, int maxWidth)
    {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for(String word : paragraph.split(" "))
        {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if(getRenderAdapter().getTextWidth(candidate) > maxWidth && !line.isEmpty())
            {
                lines.add(line.toString());
                line = new StringBuilder(word);
            }
            else
            {
                line = new StringBuilder(candidate);
            }
        }
        lines.add(line.toString());
        return lines;
    }

    public static class TutorialScreenData implements ScreenData
    {
        @Override
        public void load(JsonObject jsonObject) {}

        @Override
        public JsonObject save() { return new JsonObject(); }
    }
}
