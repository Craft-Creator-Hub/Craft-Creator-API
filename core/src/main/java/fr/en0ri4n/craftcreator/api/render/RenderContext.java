package fr.en0ri4n.craftcreator.api.render;

/**
 * Opaque, platform-independent render context passed from platform screens into core renderers.
 *
 * Core code must treat this as an opaque token and never reference platform classes.
 * Platform implementations (e.g. Forge) provide a concrete implementation that wraps
 * PoseStack / MultiBufferSource / partialTicks, etc.
 */
public interface RenderContext {
    // marker interface by design — keep core independent from platform details
}