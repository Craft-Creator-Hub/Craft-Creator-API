package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Themed tooltip built from typed lines instead of formatting codes:
 * <pre>
 * CoreTooltip.create()
 *         .title("Iron Ingot", Identifier.from("minecraft:iron_ingot"))
 *         .subtitle("minecraft:smelting")
 *         .separator()
 *         .entry("Cooking time", "200")
 *         .render(ctx, mouseX, mouseY);
 * </pre>
 * Long texts are wrapped, and the tooltip stays inside the screen and above items.
 */
public class CoreTooltip
{
    private static final int MAX_WIDTH = 220;
    private static final int PADDING = 5;
    private static final int LINE_HEIGHT = 10;
    private static final int ICON_SIZE = 10;
    private static final int ICON_GAP = 3;
    private static final int SEPARATOR_HEIGHT = 7;
    private static final int ENTRY_GAP = 8;
    private static final float LAYER = 400F;
    private static final int BACKGROUND = 0xF0141414;
    private static final int OUTER_BORDER = 0xFF0A0A0A;
    private static final int INNER_BORDER = 0xFF3E3E3E;

    private final List<Line> lines = new ArrayList<>();

    private enum Kind { TEXT, ENTRY, SEPARATOR }

    /**
     * @param value only for entries
     * @param icon  item drawn before the text, may be null
     */
    private record Line(Kind kind, String text, String value, int color, Identifier icon) {}

    public static CoreTooltip create()
    {
        return new CoreTooltip();
    }

    /**
     * A tooltip with a single, wrapped text.
     */
    public static CoreTooltip of(String text)
    {
        return create().text(text, CoreTheme.TEXT);
    }

    public CoreTooltip title(String text)
    {
        return title(text, null);
    }

    public CoreTooltip title(String text, Identifier icon)
    {
        lines.add(new Line(Kind.TEXT, text, null, CoreTheme.TEXT_BRIGHT, icon));
        return this;
    }

    public CoreTooltip subtitle(String text)
    {
        return text(text, CoreTheme.TEXT_MUTED);
    }

    public CoreTooltip text(String text, int color)
    {
        lines.add(new Line(Kind.TEXT, text, null, color, null));
        return this;
    }

    /**
     * A line starting with the item's icon.
     */
    public CoreTooltip item(Identifier itemId, String text, int color)
    {
        lines.add(new Line(Kind.TEXT, text, null, color, itemId));
        return this;
    }

    /**
     * "label     value" on one line, label muted and value bright.
     */
    public CoreTooltip entry(String label, String value)
    {
        return entry(label, value, CoreTheme.TEXT_BRIGHT);
    }

    public CoreTooltip entry(String label, String value, int valueColor)
    {
        lines.add(new Line(Kind.ENTRY, label, value, valueColor, null));
        return this;
    }

    public CoreTooltip separator()
    {
        // No separator first, last or twice in a row
        if(!lines.isEmpty() && lines.get(lines.size() - 1).kind() != Kind.SEPARATOR)
            lines.add(new Line(Kind.SEPARATOR, "", null, 0, null));
        return this;
    }

    public boolean isEmpty()
    {
        return lines.stream().noneMatch(line -> line.kind() != Kind.SEPARATOR);
    }

    /* ---------------- rendering ---------------- */

    /**
     * Lines ready to draw: texts wrapped to the max width, trailing separator removed.
     */
    private List<Line> layoutLines(RenderAdapter adapter)
    {
        List<Line> laidOut = new ArrayList<>();
        for(Line line : lines)
        {
            if(line.kind() != Kind.TEXT)
            {
                laidOut.add(line);
                continue;
            }

            int available = MAX_WIDTH - (line.icon() != null ? ICON_SIZE + ICON_GAP : 0);
            List<String> wrapped = wrap(adapter, line.text(), available);
            for(int i = 0; i < wrapped.size(); i++)
                laidOut.add(new Line(Kind.TEXT, wrapped.get(i), null, line.color(), i == 0 ? line.icon() : null));
        }
        while(!laidOut.isEmpty() && laidOut.get(laidOut.size() - 1).kind() == Kind.SEPARATOR)
            laidOut.remove(laidOut.size() - 1);
        return laidOut;
    }

    private static int lineWidth(RenderAdapter adapter, Line line, int labelWidth)
    {
        return switch(line.kind())
        {
            case TEXT -> adapter.getTextWidth(line.text()) + (line.icon() != null ? ICON_SIZE + ICON_GAP : 0);
            case ENTRY -> labelWidth + ENTRY_GAP + adapter.getTextWidth(line.value());
            case SEPARATOR -> 0;
        };
    }

    private static int lineHeight(Line line)
    {
        return line.kind() == Kind.SEPARATOR ? SEPARATOR_HEIGHT : LINE_HEIGHT;
    }

    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        if(isEmpty()) return;

        RenderAdapter adapter = CraftCreatorAPI.get().getPlatform().getRenderAdapter();
        List<Line> laidOut = layoutLines(adapter);

        // Entry values are aligned in a column after the longest label
        int labelWidth = laidOut.stream().filter(line -> line.kind() == Kind.ENTRY).mapToInt(line -> adapter.getTextWidth(line.text())).max().orElse(0);
        int contentWidth = Math.min(MAX_WIDTH, laidOut.stream().mapToInt(line -> lineWidth(adapter, line, labelWidth)).max().orElse(0));
        int contentHeight = laidOut.stream().mapToInt(CoreTooltip::lineHeight).sum() - 2;

        int width = contentWidth + PADDING * 2;
        int height = contentHeight + PADDING * 2;

        // Right of and above the mouse, flipped when it would leave the screen
        int x = mouseX + 12;
        int y = mouseY - 12;
        if(x + width > adapter.getScreenWidth() - 2) x = Math.max(2, mouseX - 12 - width);
        if(y + height > adapter.getScreenHeight() - 2) y = adapter.getScreenHeight() - 2 - height;
        y = Math.max(2, y);

        adapter.pushLayer(ctx, LAYER);

        adapter.drawRect(ctx, x - 1, y - 1, width + 2, height + 2, OUTER_BORDER);
        adapter.drawRect(ctx, x, y, width, height, BACKGROUND);
        adapter.drawRect(ctx, x, y, width, 1, INNER_BORDER);
        adapter.drawRect(ctx, x, y + height - 1, width, 1, INNER_BORDER);
        adapter.drawRect(ctx, x, y, 1, height, INNER_BORDER);
        adapter.drawRect(ctx, x + width - 1, y, 1, height, INNER_BORDER);

        int lineX = x + PADDING;
        int lineY = y + PADDING;
        for(Line line : laidOut)
        {
            switch(line.kind())
            {
                case TEXT ->
                {
                    int textX = lineX;
                    if(line.icon() != null)
                    {
                        adapter.drawItem(ctx, new CoreItemStack(line.icon(), 1), lineX, lineY - 1, ICON_SIZE / 16F);
                        textX += ICON_SIZE + ICON_GAP;
                    }
                    adapter.drawText(ctx, line.text(), textX, lineY, line.color());
                }
                case ENTRY ->
                {
                    adapter.drawText(ctx, line.text(), lineX, lineY, CoreTheme.TEXT_MUTED);
                    String value = trim(adapter, line.value(), contentWidth - labelWidth - ENTRY_GAP);
                    adapter.drawText(ctx, value, lineX + labelWidth + ENTRY_GAP, lineY, line.color());
                }
                case SEPARATOR -> adapter.drawRect(ctx, lineX, lineY + 2, contentWidth, 1, INNER_BORDER);
            }
            lineY += lineHeight(line);
        }

        adapter.popLayer(ctx);
    }

    /**
     * Split a text into lines fitting the width, breaking between words (or inside words longer than a line).
     */
    private static List<String> wrap(RenderAdapter adapter, String text, int maxWidth)
    {
        List<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for(String word : text.split(" "))
        {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if(adapter.getTextWidth(candidate) <= maxWidth)
            {
                line = new StringBuilder(candidate);
                continue;
            }

            if(!line.isEmpty()) result.add(line.toString());
            line = new StringBuilder(word);
            // A single word wider than the line (e.g. a long id) is cut
            while(adapter.getTextWidth(line.toString()) > maxWidth && line.length() > 1)
            {
                int cut = line.length();
                while(cut > 1 && adapter.getTextWidth(line.substring(0, cut)) > maxWidth) cut--;
                result.add(line.substring(0, cut));
                line = new StringBuilder(line.substring(cut));
            }
        }
        if(!line.isEmpty()) result.add(line.toString());
        return result;
    }

    private static String trim(RenderAdapter adapter, String text, int maxWidth)
    {
        if(adapter.getTextWidth(text) <= maxWidth) return text;
        int end = text.length();
        while(end > 0 && adapter.getTextWidth(text.substring(0, end) + "...") > maxWidth) end--;
        return text.substring(0, end) + "...";
    }
}
