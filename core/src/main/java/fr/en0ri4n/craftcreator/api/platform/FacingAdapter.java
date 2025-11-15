package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.init.shapes.CoreFacing;

public interface FacingAdapter<T> {
    CoreFacing toCore(T platformFacing);

    T fromCore(CoreFacing coreFacing);
}
