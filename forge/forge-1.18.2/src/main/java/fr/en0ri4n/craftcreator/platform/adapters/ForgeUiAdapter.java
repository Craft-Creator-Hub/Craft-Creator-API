package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;

public class ForgeUiAdapter implements UiAdapter<Widget> {

    @Override
    public void openScreen(CoreScreenDefinition screenDefinition) {
        Minecraft.getInstance().tell(() ->
                Minecraft.getInstance().setScreen(new ForgeScreen(screenDefinition))
        );
    }

    @Override
    public Widget createWidget(CoreUiElement element)
    {
        return null;
    }
}