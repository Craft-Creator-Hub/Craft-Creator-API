package fr.en0ri4n.craftcreator.platform.factory;

import fr.en0ri4n.craftcreator.impl.block.CCBlock;

public interface BlockFactory<Block>
{
    Block createBlock(CCBlock ccBlock);
}
