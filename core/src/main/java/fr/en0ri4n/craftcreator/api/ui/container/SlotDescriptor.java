package fr.en0ri4n.craftcreator.api.ui.container;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SlotDescriptor {
    public enum SlotType {
        PLAYER,
        HOTBAR,
        CUSTOM,
        GHOST
    }

    private final SlotType type;
    private final int x;
    private final int y;
    private final int index;      // index inside that group (e.g., 0..26 for player inventory)
    private final Identifier id;  // optional logical id (e.g. "input_0", "output", etc.)
}