package fr.en0ri4n.craftcreator.ui.container;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.utils.Identifier;

public final class RecipeCreatorContainerLayouts {

    private RecipeCreatorContainerLayouts() {}

    public static ContainerLayout basicRecipeEditor() {
        ContainerLayout layout = new ContainerLayout().size(176, 166);

        // Example: 3x3 input grid at (30,17)
        int idx = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                layout.addSlot(new SlotDescriptor(
                        SlotDescriptor.SlotType.CUSTOM,
                        30 + col * 18,
                        17 + row * 18,
                        idx++,
                        Identifier.from("craftcreator", "input_" + idx)
                ));
            }
        }

        // add output, player inventory, etc.
        return layout;
    }
}