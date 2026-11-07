package fr.en0ri4n.craftcreator.platform.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.platform.Forge1192Platform;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.item.ForgeItemStackAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ForgeRenderAdapter implements RenderAdapter
{
    private final Minecraft mc =  Minecraft.getInstance();

    private static final ForgeRenderAdapter INSTANCE = new ForgeRenderAdapter();

    public static ForgeRenderAdapter get()
    {
        return INSTANCE;
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

        if(slot == null)
            return null;

        if(!(slot.container instanceof ForgeGenericBlockEntity))
            return null;

        if(!slot.hasItem())
            return null;

        return Pair.of(slot.index, Forge1192Platform.get().getItemStackAdapter().fromPlatform(slot.getItem()));
    }

    @Override
    public void drawTexture(RenderContext ctx,
                            Identifier textureId,
                            int x, int y, int width, int height,
                            int textureWidth, int textureHeight,
                            int textureX, int textureY, int widthInTexture, int heightInTexture)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        bindTexture(textureId);
        Screen.blit(forgeRenderContext.poseStack(), x, y, width, height, textureX, textureY, widthInTexture, heightInTexture, textureWidth, textureHeight);
    }

    @Override
    public void drawText(RenderContext ctx, String text, int x, int y, int color)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        GuiComponent.drawString(forgeRenderContext.poseStack(), mc.font, text, x, y, color);
    }

    @Override
    public void drawText(RenderContext ctx, String text, boolean shadow, int x, int y, int color)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        if(shadow)
            mc.font.drawShadow(forgeRenderContext.poseStack(), text, x, y, color);
        else
            mc.font.draw(forgeRenderContext.poseStack(), text, x, y, color);
    }

    @Override
    public void drawRect(RenderContext ctx, int x, int y, int width, int height, int argb)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        Screen.fill(forgeRenderContext.poseStack(), x, y, x + width, y + height, argb);
    }

    @Override
    public void drawItem(RenderContext ctx, CoreItemStack item, int x, int y, float scale)
    {
        ItemStack platformStack = ForgeItemStackAdapter.get().toPlatform(item);
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().scale(scale, scale, 1.0F);
        mc.getItemRenderer().renderAndDecorateItem(platformStack, Math.round(x / scale), Math.round(y / scale));
        RenderSystem.getModelViewStack().popPose();
        // The item renderer re-applies the stack while our scale is still on it: re-apply after removing it,
        // otherwise everything drawn next (text, shapes) stays scaled
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    public void drawTooltip(RenderContext ctx, String tooltip, int mouseX, int mouseY)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        assert mc.screen != null;
        mc.screen.renderTooltip(forgeRenderContext.poseStack(), Component.literal(tooltip), mouseX, mouseY);
    }

    @Override
    public void drawTooltips(RenderContext ctx, List<String> tooltips, int mouseX, int mouseY)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        assert mc.screen != null;
        List<Component> tooltipComponents = tooltips.stream().map(Component::literal).map(textComponent -> (Component) textComponent).toList();
        mc.screen.renderTooltip(forgeRenderContext.poseStack(), tooltipComponents, Optional.empty(), mouseX, mouseY, null, ItemStack.EMPTY);
    }

    @Override
    public void drawItemTooltip(RenderContext ctx, CoreItemStack item, int mouseX, int mouseY)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        assert mc.screen != null;
        ItemStack platformStack = ForgeItemStackAdapter.get().toPlatform(item);
        mc.screen.renderTooltip(forgeRenderContext.poseStack(), mc.screen.getTooltipFromItem(platformStack), Optional.empty(), mouseX, mouseY);
    }

    @Override
    public void drawItemTooltip(RenderContext ctx, CoreItemStack item, List<String> additionalTooltips, int mouseX, int mouseY)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        assert mc.screen != null;
        ItemStack platformStack = ForgeItemStackAdapter.get().toPlatform(item);
        List<Component> tooltipComponents = new java.util.ArrayList<>(mc.screen.getTooltipFromItem(platformStack));
        tooltipComponents.addAll(additionalTooltips.stream().map(Component::literal).map(textComponent -> (Component) textComponent).toList());
        mc.screen.renderTooltip(forgeRenderContext.poseStack(), tooltipComponents, Optional.empty(), mouseX, mouseY, null, platformStack);
    }

    @Override
    public void startScale(RenderContext ctx, float scaleX, float scaleY)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        forgeRenderContext.poseStack().pushPose();
        forgeRenderContext.poseStack().scale(scaleX, scaleY, 1.0F);
    }

    @Override
    public void endScale(RenderContext ctx)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        forgeRenderContext.poseStack().popPose();
    }

    /**
     * Nested clipping areas (e.g. a text input inside a scrolled panel): each one is intersected with its parent.
     */
    private final Deque<int[]> scissorStack = new ArrayDeque<>();

    @Override
    public void enableScissor(RenderContext ctx, int x, int y, int width, int height)
    {
        int[] rect = {x, y, x + width, y + height};
        int[] parent = scissorStack.peek();
        if(parent != null)
            rect = new int[] {Math.max(rect[0], parent[0]), Math.max(rect[1], parent[1]), Math.min(rect[2], parent[2]), Math.min(rect[3], parent[3])};

        scissorStack.push(rect);
        applyScissor(rect);
    }

    @Override
    public void disableScissor(RenderContext ctx)
    {
        scissorStack.poll();
        int[] parent = scissorStack.peek();
        if(parent != null) applyScissor(parent);
        else RenderSystem.disableScissor();
    }

    private void applyScissor(int[] rect)
    {
        // RenderSystem works in window pixels with the origin at the bottom left
        double scale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        RenderSystem.enableScissor((int) (rect[0] * scale), (int) (windowHeight - rect[3] * scale),
                                   Math.max(0, (int) ((rect[2] - rect[0]) * scale)), Math.max(0, (int) ((rect[3] - rect[1]) * scale)));
    }

    @Override
    public void pushLayer(RenderContext ctx, float z)
    {
        ForgeRenderContext.from(ctx).poseStack().pushPose();
        ForgeRenderContext.from(ctx).poseStack().translate(0, 0, z);
        // Items don't use the pose stack, they have their own offset
        mc.getItemRenderer().blitOffset += z;
        layerOffsets.push(z);
    }

    @Override
    public void popLayer(RenderContext ctx)
    {
        Float z = layerOffsets.poll();
        if(z == null) return;
        ForgeRenderContext.from(ctx).poseStack().popPose();
        mc.getItemRenderer().blitOffset -= z;
    }

    private final Deque<Float> layerOffsets = new ArrayDeque<>();

    @Override
    public String getItemName(CoreItemStack item)
    {
        return ForgeItemStackAdapter.get().toPlatform(item).getHoverName().getString();
    }

    private void bindTexture(Identifier backgroundTexture)
    {
        RenderSystem.setShaderTexture(0, Objects.requireNonNull(ResourceLocation.tryParse(backgroundTexture.toString())));
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
