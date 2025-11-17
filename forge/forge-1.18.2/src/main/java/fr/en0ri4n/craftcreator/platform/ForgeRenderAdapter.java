package fr.en0ri4n.craftcreator.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.platform.RenderAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class ForgeRenderAdapter implements RenderAdapter
{
    private final Minecraft mc =  Minecraft.getInstance();
    private PoseStack currentPoseStack;

    private static final ForgeRenderAdapter INSTANCE = new ForgeRenderAdapter();

    public static ForgeRenderAdapter getInstance()
    {
        return INSTANCE;
    }

    public void setCurrentPoseStack(PoseStack poseStack)
    {
        this.currentPoseStack = poseStack;
    }

    public PoseStack getCurrentPoseStack()
    {
        return currentPoseStack;
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
    public void renderTexture(Identifier textureId, float x, float y, float width, float height, int textureWidth, int textureHeight, float u, float v, float uWidth, float vHeight, float z)
    {
        bindTexture(textureId);
        float uScale = 1.0f / textureWidth;
        float vScale = 1.0f / textureHeight;
        Screen.blit(currentPoseStack, (int)x, (int)y, 0, u * uScale, v * vScale, (int)width, (int)height, textureHeight, textureWidth);
    }

    @Override
    public void renderText(String text, float x, float y, int color, float z)
    {

    }

    @Override
    public void renderRect(float x, float y, float width, float height, int argb, float z)
    {

    }

    @Override
    public void renderItem(Identifier itemId, float x, float y, float z)
    {

    }

    @Override
    public void bindTexture(Identifier backgroundTexture)
    {
        RenderSystem.setShaderTexture(0, Objects.requireNonNull(ResourceLocation.tryParse(backgroundTexture.toString())));
    }
}
