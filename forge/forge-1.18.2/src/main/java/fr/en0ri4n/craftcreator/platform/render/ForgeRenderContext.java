package fr.en0ri4n.craftcreator.platform.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import fr.en0ri4n.craftcreator.api.render.RenderContext;

/**
 * Concrete Forge render context that wraps the PoseStack and an optional MultiBufferSource.
 * Create one each frame inside Screen.render(...) and pass it down to core rendering code.
 *
 * Example usage in a Screen:
 *   ForgeRenderContext ctx = new ForgeRenderContext(poseStack, mc.renderBuffers().bufferSource(), partialTicks);
 *   CraftCreatorAPI.getInstance().getPlatform().getRenderAdapter().renderTexture(ctx, ...);
 */
public class ForgeRenderContext implements RenderContext {

    private final PoseStack poseStack;
    private final MultiBufferSource.BufferSource bufferSource;
    private final float partialTicks;

    /**
     * Create a new context for the current frame.
     *
     * @param poseStack    the current PoseStack from Screen.render (must be non-null)
     * @param bufferSource the current MultiBufferSource.BufferSource (may be null if not used)
     * @param partialTicks render partial ticks (pass-through)
     */
    public ForgeRenderContext(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTicks) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.partialTicks = partialTicks;
    }

    /**
     * The PoseStack provided by the screen render loop. Never cache this beyond the frame.
     */
    public PoseStack poseStack() {
        return poseStack;
    }

    /**
     * The buffer source for batched rendering (may be null on some call sites).
     */
    public MultiBufferSource.BufferSource bufferSource() {
        return bufferSource;
    }

    /**
     * Partial ticks value from the screen render call.
     */
    public float partialTicks() {
        return partialTicks;
    }

    public static ForgeRenderContext from(RenderContext ctx) {
        if (!(ctx instanceof ForgeRenderContext forgeCtx)) {
            throw new IllegalArgumentException("Expected ForgeRenderContext, got: " + ctx.getClass().getName());
        }
        return forgeCtx;
    }
}