package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeWidget;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;

public class ForgeUiAdapter implements UiAdapter<Widget>
{
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ForgeUiAdapter INSTANCE = new ForgeUiAdapter();

    public static ForgeUiAdapter get()
    {
        return INSTANCE;
    }

    @Override
    public void openScreen(CoreScreenDefinition<?> screenDefinition)
    {
        mc.tell(() -> mc.setScreen(new ForgeScreen(screenDefinition)));
    }

    @Override
    public boolean isCtrlKeyDown()
    {
        return Screen.hasControlDown();
    }

    @Override
    public Widget createWidget(CoreUiElement element)
    {
        return new ForgeWidget(element);
    }
}