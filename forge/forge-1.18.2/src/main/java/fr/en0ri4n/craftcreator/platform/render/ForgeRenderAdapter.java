package fr.en0ri4n.craftcreator.platform.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.item.ForgeItemStackAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

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

        return Pair.of(slot.index, Forge1182Platform.get().getItemStackAdapter().fromPlatform(slot.getItem()));
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
        mc.font.drawShadow(forgeRenderContext.poseStack(), text, x, y, color);
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
        mc.getItemRenderer().renderAndDecorateItem(platformStack, (int) (x / scale), (int) (y / scale));
        RenderSystem.getModelViewStack().popPose();
    }

    @Override
    public void drawTooltip(RenderContext ctx, String tooltip, int mouseX, int mouseY)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        assert mc.screen != null;
        mc.screen.renderTooltip(forgeRenderContext.poseStack(), new TextComponent(tooltip), mouseX, mouseY);
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
