package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.block.CCBlock;
import fr.en0ri4n.craftcreator.api.platform.BlockAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeBlockAdapter implements BlockAdapter<Block> {

    @Override
    public CCBlock toCore(Identifier id) {
        return new CCBlock(id);
    }

    @Override
    public Block toPlatform(CCBlock block) {
        Identifier id = block.getRegistryName();
        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        return ForgeRegistries.BLOCKS.getValue(rl);
    }

    @Override
    public CCBlock fromPlatform(Block platformBlock) {
        ResourceLocation rl = ForgeRegistries.BLOCKS.getKey(platformBlock);
        if (rl == null) {
            return null;
        }
        return new CCBlock(new Identifier(rl.getNamespace(), rl.getPath()));
    }
}
