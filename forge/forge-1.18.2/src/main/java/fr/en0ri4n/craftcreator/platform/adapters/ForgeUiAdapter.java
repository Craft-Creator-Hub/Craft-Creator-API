package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.screen.ForgeScreenRenderer;
import net.minecraft.client.Minecraft;

public class ForgeUiAdapter implements UiAdapter {

    @Override
    public void openScreen(CoreScreenDefinition screenDefinition) {
        Minecraft.getInstance().tell(() ->
                Minecraft.getInstance().setScreen(new ForgeScreenRenderer(screenDefinition))
        );
    }
}