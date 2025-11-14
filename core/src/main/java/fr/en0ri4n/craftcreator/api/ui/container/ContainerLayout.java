package fr.en0ri4n.craftcreator.api.ui.container;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ContainerLayout {

    private final List<SlotDescriptor> slots = new ArrayList<>();
    private int width = 176;   // logical GUI width
    private int height = 166;  // logical GUI height

    public ContainerLayout addSlot(SlotDescriptor slot) {
        slots.add(slot);
        return this;
    }

    public ContainerLayout size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
}