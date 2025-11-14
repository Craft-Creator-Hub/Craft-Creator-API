package fr.en0ri4n.craftcreator.api.ui;

/**
 * Core UI layout/logic for a particular screen.
 * This contains NO Forge/Fabric classes.
 */
public interface ScreenRenderer {
    void render(VirtualScreen screen, int mouseX, int mouseY, float partialTicks);
}