package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.ui.elements.*;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeButton;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeDropdownWidget;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeSimpleListWidget;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class ForgeUiAdapter implements UiAdapter<Widget>
{
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ForgeUiAdapter INSTANCE = new ForgeUiAdapter();
    public static ForgeUiAdapter get() { return INSTANCE; }

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
    public Widget createWidget(CoreUiElement element, int leftPos, int topPos, CoreScreenDefinition<?> screenDefinition)
    {
        return switch(element.getType())
        {
            case BUTTON -> new ForgeButton((CoreButton) element);
            case TEXT_INPUT -> addTextInputWidget((CoreTextInput) element, leftPos, topPos, screenDefinition);
            case DROPDOWN -> new ForgeDropdownWidget((CoreDropdown) element);
            case LIST -> new ForgeSimpleListWidget((CoreList) element);
        };
    }

    private Widget addTextInputWidget(CoreTextInput text, int leftPos, int topPos, CoreScreenDefinition<?> screenDefinition)
    {
        int x = leftPos + text.getX();
        int y = topPos + text.getY();
        int w = text.getWidth();
        int h = text.getHeight();

        EditBox box = new EditBox(mc.font, x, y, w, h, new TextComponent(""));
        box.setMaxLength(text.getMaxLength());
        box.setValue(text.getValue() != null ? text.getValue() : "");
        return box;
    }
}