package fr.en0ri4n.craftcreator.platform.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

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
    public void renderTexture(RenderContext ctx, Identifier textureId, float x, float y, float width, float height, int textureWidth, int textureHeight, float u, float v, float uWidth, float vHeight, float z)
    {
        ForgeRenderContext forgeRenderContext = ForgeRenderContext.from(ctx);
        bindTexture(ctx, textureId);
        float uScale = 1.0f / textureWidth;
        float vScale = 1.0f / textureHeight;
        Screen.blit(forgeRenderContext.poseStack(), (int)x, (int)y, 0, u * uScale, v * vScale, (int)width, (int)height, textureHeight, textureWidth);
    }

    @Override
    public void renderText(RenderContext ctx, String text, float x, float y, int color, float z)
    {

    }

    @Override
    public void renderRect(RenderContext ctx, float x, float y, float width, float height, int argb, float z)
    {

    }

    @Override
    public void renderItem(RenderContext ctx, Identifier itemId, float x, float y, float z)
    {

    }

    @Override
    public void bindTexture(RenderContext ctx, Identifier backgroundTexture)
    {
        RenderSystem.setShaderTexture(0, Objects.requireNonNull(ResourceLocation.tryParse(backgroundTexture.toString())));
    }
}
