package fr.en0ri4n.craftcreator.platform;

import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.init.RegistryAdapter;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.FacingType;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreShapes;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreVoxelShape;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeRegistryAdapter implements RegistryAdapter {

    private static final ForgeRegistryAdapter instance = new ForgeRegistryAdapter();
    public static ForgeRegistryAdapter getInstance() {
        return instance;
    }

    private final DeferredRegister<Block> BLOCKS;
    private final DeferredRegister<Item> ITEMS;

    public ForgeRegistryAdapter() {
        this.BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CCReferences.MOD_ID);
        this.ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CCReferences.MOD_ID);
    }

    /**
     * Expose the DeferredRegister instances so caller can register them on their mod event bus.
     */
    public DeferredRegister<Block> getBlockDeferredRegister() {
        return BLOCKS;
    }

    public DeferredRegister<Item> getItemDeferredRegister() {
        return ITEMS;
    }

    @Override
    public void registerBlock(CoreBlockDef blockDef) {
        ResourceLocation rl = toRL(blockDef.getId());
        String name = rl.getPath();

        BLOCKS.register(name, () -> createBlockFromProps(blockDef));
    }

    @Override
    public void registerItem(CoreItemDef itemDef) {
        CraftCreator.getInstance().getPlatform().getLogger().info("Registering item: " + itemDef.getId());
        ResourceLocation rl = toRL(itemDef.getId());
        String name = rl.getPath();

        Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_MISC);
        if (itemDef.getMaxStackSize() != 64) props.stacksTo(itemDef.getMaxStackSize());

        ITEMS.register(name, () -> new Item(props));
    }

    @Override
    public void registerBlockItem(CoreBlockItemDef blockItemDef) {
        CoreBlockDef blockDef = blockItemDef.getBlock();
        ResourceLocation rl = toRL(blockDef.getId());
        String name = rl.getPath();

        // Register block first
        RegistryObject<Block> regBlock = BLOCKS.register(name, () -> createBlockFromProps(blockDef));

        // If the combined def includes an item, register a BlockItem that points to the registered block.
        if (blockItemDef.hasItem()) {
            CoreItemDef itemDef = blockItemDef.getItem();
            Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS);
            if (itemDef.getMaxStackSize() != 64) props.stacksTo(itemDef.getMaxStackSize());

            ITEMS.register(name, () -> new BlockItem(regBlock.get(), props));
        }
    }

    private ResourceLocation toRL(Identifier id) {
        return CraftCreator.getInstance().getPlatform().getIdentifierAdapter().fromCore(id);
    }

    private Block createBlockFromProps(CoreBlockDef def) {
        FacingType facing = def.getFacingType();

        BlockBehaviour.Properties props = BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.STONE);
        // parse other def.getProperties() into props as needed

        if (!def.isRotateModel())
            return new Block(props);

        return switch(facing)
        {
            case HORIZONTAL -> new Block(props)
            {
                @Override
                public RenderShape getRenderShape(BlockState pState)
                {
                    return RenderShape.MODEL;
                }

                @Override
                public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
                {
                    CoreVoxelShape coreShape = def.getFacingShapes().getOrDefault(
                            CraftCreator.getInstance().getPlatform().getFacingAdapter().toCore(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)),
                            CoreShapes.FULL);

                    return CraftCreator.getInstance().getPlatform().getBlockShapeAdapter().toPlatformShape(coreShape);
                }

                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder)
                {
                    builder.add(BlockStateProperties.HORIZONTAL_FACING);
                }

                @Override
                public net.minecraft.world.level.block.state.BlockState getStateForPlacement(BlockPlaceContext ctx)
                {
                    Direction dir = ctx.getHorizontalDirection().getOpposite();
                    return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir);
                }
            };
            default -> new Block(props);
        };
    }
}