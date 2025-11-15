package fr.en0ri4n.craftcreator.api.ui;

/**
 * Opaque marker for a platform render context. Implementations wrap platform-specific
 * rendering objects (e.g. Forge PoseStack / MultiBufferSource).
 * <p>
 * Core code only passes this object to RenderAdapter methods; it must not inspect or cast it.
 */
public interface RenderContext {
    // marker interface on purpose — keep core independent from platform types
}