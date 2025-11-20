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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ForgeRenderAdapter implements RenderAdapter
{
    private final Minecraft mc =  Minecraft.getInstance();

    private static final ForgeRenderAdapter INSTANCE = new ForgeRenderAdapter();

    public static ForgeRenderAdapter getInstance()
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
        bindTexture(ctx, textureId);
        Screen.blit(forgeRenderContext.poseStack(), x, y, width, height, textureX, textureY, widthInTexture, heightInTexture, textureWidth, textureHeight);
    }

    @Override
    public void drawText(RenderContext ctx, String text, int x, int y, int color)
    {

    }

    @Override
    public void drawRect(RenderContext ctx, int x, int y, int width, int height, int argb)
    {

    }

    @Override
    public void drawItem(RenderContext ctx, CoreItemStack item, int x, int y)
    {
        ItemStack platformStack = ForgeItemStackAdapter.get().toPlatform(item);

        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        mc.getItemRenderer().renderAndDecorateItem(platformStack, x, y);
    }

    @Override
    public void bindTexture(RenderContext ctx, Identifier backgroundTexture)
    {
        RenderSystem.setShaderTexture(0, Objects.requireNonNull(ResourceLocation.tryParse(backgroundTexture.toString())));
    }
}
