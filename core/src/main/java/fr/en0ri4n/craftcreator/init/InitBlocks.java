package fr.en0ri4n.craftcreator.init;

import fr.en0ri4n.craftcreator.impl.block.CCBlock;
import fr.en0ri4n.craftcreator.platform.RegistryBridge;

import java.util.ArrayList;
import java.util.List;

public abstract class InitBlocks
{
    private final RegistryBridge registry;

    protected abstract List<CCBlock> getBlocks();

    public InitBlocks(RegistryBridge registry)
    {
        this.registry = registry;
    }

    public void registerAll()
    {
        List<CCBlock> blocks = new ArrayList<>(getBlocks());
        for (CCBlock block : blocks)
        {
            registry.registerBlock(block);
        }
    }
}
