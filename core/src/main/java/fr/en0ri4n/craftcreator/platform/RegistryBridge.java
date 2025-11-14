package fr.en0ri4n.craftcreator.platform;

import fr.en0ri4n.craftcreator.impl.block.CCBlock;
import fr.en0ri4n.craftcreator.impl.item.CCItem;
import fr.en0ri4n.craftcreator.utils.Identifier;

public interface RegistryBridge
{
    void registerBlock(CCBlock block);
    CCBlock getBlock(Identifier id);

    void registerItem(CCItem item);
    CCItem getItem(Identifier id);
}
