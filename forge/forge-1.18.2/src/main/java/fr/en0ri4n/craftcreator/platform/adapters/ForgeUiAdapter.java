package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.ui.elements.*;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeDropdownWidget;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeSimpleListWidget;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
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
            case BUTTON -> addButtonWidget((CoreButton) element, leftPos, topPos, screenDefinition);
            case TEXT_INPUT -> addTextInputWidget((CoreTextInput) element, leftPos, topPos, screenDefinition);
            case DROPDOWN -> addDropdownWidget((CoreDropdown) element, leftPos, topPos, screenDefinition);
            case LIST -> addListWidget((CoreList) element, leftPos, topPos, screenDefinition);
        };
    }

    private Widget addButtonWidget(CoreButton btn, int leftPos, int topPos, CoreScreenDefinition<?> screenDefinition)
    {
        int x = leftPos + btn.getX();
        int y = topPos + btn.getY();
        int w = btn.getWidth();
        int h = btn.getHeight();

        Button mcButton = new Button(x, y, w, h, new TextComponent(btn.getLabel()), b -> screenDefinition.onButtonPressed(btn.getId(), btn.getActionId()));
        mcButton.active = btn.isEnabled();
        return mcButton; // ForgeButton needed with CoreElementListener
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

    private Widget addDropdownWidget(CoreDropdown dropdown, int leftPos, int topPos, CoreScreenDefinition<?> screenDefinition)
    {
        int x = leftPos + dropdown.getX();
        int y = topPos + dropdown.getY();
        int w = dropdown.getWidth();
        int h = dropdown.getHeight();

        return new ForgeDropdownWidget(x, y, w, h, dropdown, (selectedIndex, selectedValue) -> screenDefinition.onDropdownChanged(dropdown.getId(), selectedIndex, selectedValue));
    }

    private Widget addListWidget(CoreList list, int leftPos, int topPos, CoreScreenDefinition<?> screenDefinition)
    {
        int x = leftPos + list.getX();
        int y = topPos + list.getY();
        int w = list.getWidth();
        int h = list.getHeight();

        ForgeSimpleListWidget widget = new ForgeSimpleListWidget(w, h, y, y + h, 12, list);
        widget.setLeftPos(x);
        return widget;
    }
}