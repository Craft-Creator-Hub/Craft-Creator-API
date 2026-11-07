package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Short messages shown at the top of a screen (e.g. "the recipe is incomplete"), stacked and fading out after a few seconds.
 */
public class CoreNotifications
{
    private static final long DURATION_MS = 4000;
    private static final long FADE_MS = 600;
    private static final int MAX_SHOWN = 4;
    private static final int MAX_WIDTH = 240;
    private static final int PADDING = 5;
    private static final int ACCENT_WIDTH = 3;
    private static final int LINE_HEIGHT = 10;
    private static final int GAP = 3;

    public enum Kind
    {
        SUCCESS(CoreTheme.TEXT_SUCCESS),
        ERROR(CoreTheme.TEXT_ERROR),
        INFO(CoreTheme.ACCENT);

        private final int color;

        Kind(int color)
        {
            this.color = color;
        }
    }

    private record Notification(String text, Kind kind, long createdAt) {}

    private final List<Notification> notifications = new ArrayList<>();

    public void push(String text, Kind kind)
    {
        if(text == null || text.isBlank()) return;
        notifications.add(new Notification(stripFormatting(text), kind, System.currentTimeMillis()));
        while(notifications.size() > MAX_SHOWN)
            notifications.remove(0);
    }

    /**
     * Draw the notifications stacked downwards from (centerX, top), newest at the bottom.
     */
    public void render(RenderContext ctx, RenderAdapter adapter, int centerX, int top)
    {
        long now = System.currentTimeMillis();
        notifications.removeIf(notification -> now - notification.createdAt() > DURATION_MS);
        if(notifications.isEmpty()) return;

        adapter.pushLayer(ctx, 400F);
        int y = top;
        for(Iterator<Notification> it = notifications.iterator(); it.hasNext(); )
        {
            Notification notification = it.next();
            List<String> lines = wrap(adapter, notification.text(), MAX_WIDTH - PADDING * 2 - ACCENT_WIDTH);
            int textWidth = lines.stream().mapToInt(adapter::getTextWidth).max().orElse(0);
            int width = textWidth + PADDING * 2 + ACCENT_WIDTH + 2;
            int height = lines.size() * LINE_HEIGHT + PADDING * 2 - 2;
            int x = centerX - width / 2;

            // Fade out at the end
            long left = DURATION_MS - (now - notification.createdAt());
            float alpha = Math.min(1F, left / (float) FADE_MS);
            int a = Math.max(8, Math.round(alpha * 0xE8));

            adapter.drawRect(ctx, x - 1, y - 1, width + 2, height + 2, withAlpha(notification.kind().color, a));
            adapter.drawRect(ctx, x, y, width, height, withAlpha(0xFF141414, a));
            adapter.drawRect(ctx, x, y, ACCENT_WIDTH, height, withAlpha(notification.kind().color, a));
            if(alpha > 0.15F)
            {
                int textColor = withAlpha(CoreTheme.TEXT_BRIGHT, Math.max(0x20, Math.round(alpha * 0xFF)));
                for(int i = 0; i < lines.size(); i++)
                    adapter.drawText(ctx, lines.get(i), x + ACCENT_WIDTH + PADDING, y + PADDING + i * LINE_HEIGHT, textColor);
            }
            y += height + 2 + GAP;
        }
        adapter.popLayer(ctx);
    }

    private static int withAlpha(int color, int alpha)
    {
        return (alpha << 24) | (color & 0xFFFFFF);
    }

    /**
     * Messages come from translations with "§" color codes: the notification gives the color instead.
     */
    private static String stripFormatting(String text)
    {
        return text.replaceAll("§.", "").trim();
    }

    /**
     * Split the text on spaces into lines no wider than maxWidth (a single long word stays on its own line).
     */
    static List<String> wrap(RenderAdapter adapter, String text, int maxWidth)
    {
        List<String> lines = new ArrayList<>();
        for(String paragraph : text.split("\n"))
        {
            StringBuilder line = new StringBuilder();
            for(String word : paragraph.split(" "))
            {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if(!line.isEmpty() && adapter.getTextWidth(candidate) > maxWidth)
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
        }
        return lines;
    }
}
