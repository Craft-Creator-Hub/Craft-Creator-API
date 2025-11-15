package fr.en0ri4n.craftcreator.api.ui.container;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SlotDescriptor {
    public enum SlotType {
        PLAYER,
        HOTBAR,
        RECIPE_CREATOR_INPUT,
        RECIPE_CREATOR_OUTPUT,
        FUEL,
        CUSTOM
    }

    private final SlotType type;
    private final int x;
    private final int y;
    private final int index;      // index inside that group (e.g., 0..26 for player inventory)
    private final Identifier id;  // logical id (e.g. "input_0", "output", etc.)
}