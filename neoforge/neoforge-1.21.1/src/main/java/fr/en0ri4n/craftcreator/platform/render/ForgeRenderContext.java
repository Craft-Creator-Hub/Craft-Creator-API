package fr.en0ri4n.craftcreator.platform.render;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Render context of a frame: the GuiGraphics every 1.20 screen draws with.
 */
public class ForgeRenderContext implements RenderContext
{
    private final GuiGraphics graphics;

    public ForgeRenderContext(GuiGraphics graphics)
    {
        this.graphics = graphics;
    }

    /**
     * The GuiGraphics of the current frame. Never cache it beyond the frame.
     */
    public GuiGraphics graphics()
    {
        return graphics;
    }

    public PoseStack poseStack()
    {
        return graphics.pose();
    }

    public static ForgeRenderContext from(RenderContext ctx)
    {
        if(!(ctx instanceof ForgeRenderContext forgeCtx))
            throw new IllegalArgumentException("Expected ForgeRenderContext, got: " + ctx.getClass().getName());
        return forgeCtx;
    }

    public static ForgeRenderContext of(GuiGraphics graphics)
    {
        return new ForgeRenderContext(graphics);
    }
}
