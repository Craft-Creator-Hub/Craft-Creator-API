package fr.en0ri4n.craftcreator.api.ui;

/**
 * Receives UI actions triggered from platform GUIs.
 */
public interface CoreUiActionHandler {

    /**
     * A button was pressed.
     *
     * @param screenId   the logical screen id
     * @param elementId  id of the CoreUiElement (button) that was pressed
     * @param actionId   logical action id associated to the button
     */
    void onButtonPressed(String screenId, String elementId, String actionId);

    /**
     * Dropdown selection changed.
     *
     * @param screenId   the logical screen id
     * @param elementId  id of the dropdown
     * @param selectedIndex new selected index
     * @param selectedValue new selected value (may be null)
     */
    void onDropdownChanged(String screenId, String elementId, int selectedIndex, String selectedValue);

    /**
     * Text input changed (optional, but useful).
     */
    default void onTextChanged(String screenId, String elementId, String newValue) {}
}