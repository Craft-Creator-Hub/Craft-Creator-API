package fr.en0ri4n.craftcreator.platform.ui;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.CoreUiActionHandler;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTextInput;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeSimpleListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;

public final class ForgeScreenBuilder {

    private ForgeScreenBuilder() {}

    public static Screen build(CoreScreenDefinition coreScreen, CoreUiActionHandler handler) {
        return new BuiltScreen(coreScreen, handler);
    }

    private static class BuiltScreen extends Screen {

        private final CoreScreenDefinition coreScreen;
        private final CoreUiActionHandler handler;

        // Map core element ids to widgets if you want to sync state back
        private final Map<String, EditBox> textInputs = new HashMap<>();

        protected BuiltScreen(CoreScreenDefinition coreScreen, CoreUiActionHandler handler) {
            super(new TextComponent(coreScreen.getTitle()));
            this.coreScreen = coreScreen;
            this.handler = handler;
        }

        @Override
        protected void init() {
            super.init();

            // Centering / scaling strategy is up to you; here we use core coordinates directly.
            for (CoreUiElement element : coreScreen.getElements()) {
                if (element instanceof CoreButton btn) {
                    addButtonWidget(btn);
                } else if (element instanceof CoreTextInput text) {
                    addTextInputWidget(text);
                } else if (element instanceof CoreDropdown dropdown) {
                    addDropdownWidget(dropdown);
                } else if (element instanceof CoreList list) {
                    addListWidget(list);
                }
            }
        }

        private void addButtonWidget(CoreButton btn) {
            int x = btn.getX();
            int y = btn.getY();
            int w = btn.getWidth();
            int h = btn.getHeight();

            Button mcButton = new Button(x, y, w, h, new TextComponent(btn.getLabel()), b -> {
                handler.onButtonPressed(coreScreen.getId(), btn.getId(), btn.getActionId());
            });
            mcButton.active = btn.isEnabled();
            this.addRenderableWidget(mcButton);
        }

        private void addTextInputWidget(CoreTextInput text) {
            int x = text.getX();
            int y = text.getY();
            int w = text.getWidth();
            int h = text.getHeight();

            EditBox box = new EditBox(this.font, x, y, w, h, new TextComponent(""));
            box.setMaxLength(text.getMaxLength());
            box.setValue(text.getValue() != null ? text.getValue() : "");
            this.addRenderableWidget(box);
            textInputs.put(text.getId(), box);
        }

        private void addDropdownWidget(CoreDropdown dropdown) {
            int x = dropdown.getX();
            int y = dropdown.getY();
            int w = dropdown.getWidth();
            int h = dropdown.getHeight();

            fr.en0ri4n.craftcreator.platform.ui.ForgeDropdownWidget widget = new fr.en0ri4n.craftcreator.platform.ui.ForgeDropdownWidget(
                    x, y, w, h,
                    dropdown,
                    (selectedIndex, selectedValue) -> {
                        dropdown.setSelectedIndex(selectedIndex);
                        handler.onDropdownChanged(
                                coreScreen.getId(),
                                dropdown.getId(),
                                selectedIndex,
                                selectedValue
                        );
                    }
            );
            this.addRenderableWidget(widget);
        }

        private void addListWidget(CoreList list) {
            int x = list.getX();
            int y = list.getY();
            int w = list.getWidth();
            int h = list.getHeight();

            ForgeSimpleListWidget widget = new ForgeSimpleListWidget(
                    Minecraft.getInstance(), w, h, y, y + h, 12,
                    list,
                    (selectedIndex, value) -> {
                        list.setSelectedIndex(selectedIndex);
                        // if you want, add handler callback here too
                    });
            widget.setLeftPos(x);
            this.addRenderableWidget(widget);
        }

        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
            this.renderBackground(poseStack);
            super.render(poseStack, mouseX, mouseY, partialTicks);
            // Optionally: render tooltips based on CoreUiElement.getTooltip()
        }
    }
}