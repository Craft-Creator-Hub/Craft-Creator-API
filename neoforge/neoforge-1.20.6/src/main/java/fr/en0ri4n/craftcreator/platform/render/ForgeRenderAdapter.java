package fr.en0ri4n.craftcreator.platform.render;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.platform.NeoForge1206Platform;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.item.ForgeItemStackAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Draws core UI with the 1.20 GuiGraphics: items, text, shapes and clipping all follow its pose stack.
 */
public class ForgeRenderAdapter implements RenderAdapter
{
    private final Minecraft mc = Minecraft.getInstance();

    private static final ForgeRenderAdapter INSTANCE = new ForgeRenderAdapter();

    public static ForgeRenderAdapter get()
    {
        return INSTANCE;
    }

    private static GuiGraphics graphics(RenderContext ctx)
    {
        return ForgeRenderContext.from(ctx).graphics();
    }

    @Override
    public int getScreenWidth()
    {
        return mc.getWindow().getGuiScaledWidth();
    }

    @Override
    public int getScreenHeight()
    {
        return mc.getWindow().getGuiScaledHeight();
    }

    @Override
    public Pair<Integer, CoreItemStack> getItemStackUnderMouse(double mouseX, double mouseY)
    {
        if(!(mc.screen instanceof AbstractContainerScreen<?> containerScreen)) return null;

        Slot slot = containerScreen.getSlotUnderMouse();
        if(slot == null || !(slot.container instanceof ForgeGenericBlockEntity) || !slot.hasItem())
            return null;

        return Pair.of(slot.index, NeoForge1206Platform.get().getItemStackAdapter().fromPlatform(slot.getItem()));
    }

    @Override
    public void drawTexture(RenderContext ctx,
                            Identifier textureId,
                            int x, int y, int width, int height,
                            int textureWidth, int textureHeight,
                            int textureX, int textureY, int widthInTexture, int heightInTexture)
    {
        ResourceLocation texture = Objects.requireNonNull(ResourceLocation.tryParse(textureId.toString()));
        graphics(ctx).blit(texture, x, y, width, height, textureX, textureY, widthInTexture, heightInTexture, textureWidth, textureHeight);
    }

    @Override
    public void drawText(RenderContext ctx, String text, int x, int y, int color)
    {
        graphics(ctx).drawString(mc.font, text, x, y, color, true);
    }

    @Override
    public void drawText(RenderContext ctx, String text, boolean shadow, int x, int y, int color)
    {
        graphics(ctx).drawString(mc.font, text, x, y, color, shadow);
    }

    @Override
    public void drawRect(RenderContext ctx, int x, int y, int width, int height, int argb)
    {
        graphics(ctx).fill(x, y, x + width, y + height, argb);
    }

    @Override
    public void drawItem(RenderContext ctx, CoreItemStack item, int x, int y, float scale)
    {
        ItemStack platformStack = ForgeItemStackAdapter.get().toPlatform(item);
        GuiGraphics graphics = graphics(ctx);
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0F);
        int itemX = Math.round(x / scale);
        int itemY = Math.round(y / scale);
        graphics.renderItem(platformStack, itemX, itemY);
        graphics.renderItemDecorations(mc.font, platformStack, itemX, itemY);
        graphics.pose().popPose();
    }

    @Override
    public void drawTooltip(RenderContext ctx, String tooltip, int mouseX, int mouseY)
    {
        graphics(ctx).renderTooltip(mc.font, Component.literal(tooltip), mouseX, mouseY);
    }

    @Override
    public void drawTooltips(RenderContext ctx, List<String> tooltips, int mouseX, int mouseY)
    {
        graphics(ctx).renderComponentTooltip(mc.font, tooltips.stream().map(line -> (Component) Component.literal(line)).toList(), mouseX, mouseY);
    }

    @Override
    public void drawItemTooltip(RenderContext ctx, CoreItemStack item, int mouseX, int mouseY)
    {
        graphics(ctx).renderTooltip(mc.font, ForgeItemStackAdapter.get().toPlatform(item), mouseX, mouseY);
    }

    @Override
    public void drawItemTooltip(RenderContext ctx, CoreItemStack item, List<String> additionalTooltips, int mouseX, int mouseY)
    {
        ItemStack platformStack = ForgeItemStackAdapter.get().toPlatform(item);
        List<Component> lines = new ArrayList<>(Screen.getTooltipFromItem(mc, platformStack));
        additionalTooltips.forEach(line -> lines.add(Component.literal(line)));
        graphics(ctx).renderComponentTooltip(mc.font, lines, mouseX, mouseY, platformStack);
    }

    @Override
    public void startScale(RenderContext ctx, float scaleX, float scaleY)
    {
        graphics(ctx).pose().pushPose();
        graphics(ctx).pose().scale(scaleX, scaleY, 1.0F);
    }

    @Override
    public void endScale(RenderContext ctx)
    {
        graphics(ctx).pose().popPose();
    }

    /**
     * GuiGraphics keeps its own clipping stack, intersecting each area with its parent (e.g. a text input inside a scrolled panel).
     */
    @Override
    public void enableScissor(RenderContext ctx, int x, int y, int width, int height)
    {
        graphics(ctx).enableScissor(x, y, x + width, y + height);
    }

    @Override
    public void disableScissor(RenderContext ctx)
    {
        graphics(ctx).disableScissor();
    }

    /**
     * Items are drawn with the pose stack too since 1.20, so moving it is enough to draw over them.
     */
    @Override
    public void pushLayer(RenderContext ctx, float z)
    {
        graphics(ctx).pose().pushPose();
        graphics(ctx).pose().translate(0, 0, z);
        layerDepth++;
    }

    @Override
    public void popLayer(RenderContext ctx)
    {
        if(layerDepth == 0) return;
        layerDepth--;
        graphics(ctx).pose().popPose();
    }

    private int layerDepth = 0;

    @Override
    public String getItemName(CoreItemStack item)
    {
        return ForgeItemStackAdapter.get().toPlatform(item).getHoverName().getString();
    }

    @Override
    public int getTextWidth(String label)
    {
        return mc.font.width(label);
    }

    @Override
    public int getFontHeight()
    {
        return mc.font.lineHeight;
    }
}
