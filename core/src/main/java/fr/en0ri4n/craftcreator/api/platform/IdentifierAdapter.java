package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.utils.Identifier;

public interface IdentifierAdapter<T> {
    Identifier toCore(T loaderId);

    T fromCore(Identifier coreId);
}
