package fr.en0ri4n.craftcreator.api.ui.container;

import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.utils.Identifier;

public interface ContainerModel {

    ContainerLayout getLayout();

    CoreScreenDefinition getScreenDefinition();

    // Called when a button is pressed
    void onButtonPressed(String elementId, String actionId);

    // Called when dropdown selection changes
    void onDropdownChanged(String elementId, int index, String value);

    // Optional: text inputs
    default void onTextChanged(String elementId, String value) {}

    static void addPlayerInventorySlots(ContainerLayout layout, int startX, int startY) {
        // Player inventory (3 rows of 9)
        int idx = 9; // skip hotbar
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                layout.addSlot(new SlotDescriptor(
                        SlotDescriptor.SlotType.PLAYER,
                        startX + col * 18,
                        startY + row * 18,
                        idx++,
                        Identifier.fromMod("player_inv_" + idx)
                ));
            }
        }

        // Hotbar (1 row of 9)
        for (int i = 0; i < 9; i++) {
            layout.addSlot(new SlotDescriptor(
                    SlotDescriptor.SlotType.HOTBAR,
                    startX + i * 18,
                    startY + 58,
                    i,
                    Identifier.fromMod("hotbar_" + i)
            ));
        }
    }
}