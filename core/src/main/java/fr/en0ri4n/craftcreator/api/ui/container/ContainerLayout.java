package fr.en0ri4n.craftcreator.api.ui.container;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ContainerLayout {

    private final List<SlotDescriptor> slots = new ArrayList<>();
    private int width = 176;   // logical GUI width
    private int height = 166;  // logical GUI height

    public void addSlot(SlotDescriptor slot) {
        slots.add(slot);
    }

    public ContainerLayout size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public SlotDescriptor getSlot(int slotIndex)
    {
        return slots.stream().filter(sd -> sd.getIndex() == slotIndex).findFirst().orElse(null);
    }
}