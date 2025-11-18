package fr.en0ri4n.craftcreator.api.ui.container;

import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityBehavior;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.ui.CoreContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ContainerModel<T extends BlockEntityBehavior> {

    private CoreBlockPos blockEntityPos;

    public abstract ContainerLayout getLayout();

    public abstract CoreContainerScreenDefinition<T> getScreenDefinition();

    // Called when a button is pressed
    public abstract void onButtonPressed(String elementId, String actionId);

    // Called when dropdown selection changes
    public abstract void onDropdownChanged(String elementId, int index, String value);

    // Text inputs
    public void onTextChanged(String elementId, String value) {}

    protected void addPlayerInventorySlots(ContainerLayout layout, int startX, int startY) {
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