package fr.en0ri4n.craftcreator.container.slot;

/**
 * Represents a slot in a container within the Craft Creator API.
 *
 * @param <T> The type of item or stack contained in the slot.
 */
public interface CCSlot<T> {
    int getSlotIndex();
    T getStack();
}
