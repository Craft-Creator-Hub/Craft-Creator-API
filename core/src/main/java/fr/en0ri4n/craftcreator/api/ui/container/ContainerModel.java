package fr.en0ri4n.craftcreator.api.ui.container;

import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.utils.Identifier;

public interface ContainerModel {

    ContainerLayout getLayout();

    CoreScreenDefinition getScreenDefinition();

    // Called when a button is pressed
    void onButtonAction(String elementId, String actionId);

    // Called when dropdown selection changes
    void onDropdownChanged(String elementId, int index, String value);

    // Optional: text inputs
    default void onTextChanged(String elementId, String value) {}

    // Optional: slot click hook
    default void onSlotClick(Identifier slotId, int button, boolean shift) {}
}