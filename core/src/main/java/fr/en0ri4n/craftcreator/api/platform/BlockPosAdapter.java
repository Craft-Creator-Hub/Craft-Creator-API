package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;

public interface BlockPosAdapter<T>
{
    T fromCore(CoreBlockPos pos);

    CoreBlockPos toCore(T pos);
}
