package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.init.RegistryAdapter;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.FacingType;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreShapes;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreVoxelShape;
import fr.en0ri4n.craftcreator.impl.model.container.minecraft.CraftingTableRecipeCreatorContainerModel;
import fr.en0ri4n.craftcreator.platform.block.RecipeCreatorBlock;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeRegistryAdapter implements RegistryAdapter
{

    private static final ForgeRegistryAdapter instance = new ForgeRegistryAdapter();

    public static ForgeRegistryAdapter getInstance()
    {
        return instance;
    }

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CCReferences.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CCReferences.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, CCReferences.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CCReferences.MOD_ID);

    public static final RegistryObject<MenuType<ForgeRecipeCreatorMenu>> RECIPE_CREATOR_MENU =
            MENUS.register("recipe_creator", () -> IForgeMenuType.create(ForgeRecipeCreatorMenu::new));

    // Register the RecipeCreatorBlock
    public static final RegistryObject<Block> RECIPE_CREATOR_BLOCK =
            BLOCKS.register("recipe_creator", RecipeCreatorBlock::new);

    // Register the RecipeCreatorBlock item
    public static final RegistryObject<Item> RECIPE_CREATOR_BLOCK_ITEM =
            ITEMS.register("recipe_creator", () -> new BlockItem(RECIPE_CREATOR_BLOCK.get(), 
                    new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final RegistryObject<BlockEntityType<ForgeGenericBlockEntity>> GENERIC_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("generic_block_entity", () ->
                    BlockEntityType.Builder.of(ForgeGenericBlockEntity::new, RECIPE_CREATOR_BLOCK.get()).build(null)
            );

    /**
     * Expose the DeferredRegister instances so caller can register them on their mod event bus.
     */
    public DeferredRegister<Block> getBlockDeferredRegister()
    {
        return BLOCKS;
    }

    public DeferredRegister<Item> getItemDeferredRegister()
    {
        return ITEMS;
    }

    public DeferredRegister<MenuType<?>> getMenuDeferredRegister()
    {
        return MENUS;
    }

    @Override
    public void registerBlock(CoreBlockDef blockDef)
    {
        ResourceLocation rl = toRL(blockDef.getId());
        String name = rl.getPath();

        BLOCKS.register(name, () -> createBlockFromProps(blockDef));
    }

    @Override
    public void registerItem(CoreItemDef itemDef)
    {
        CraftCreator.getInstance().getPlatform().getLogger().info("Registering item: " + itemDef.getId());
        ResourceLocation rl = toRL(itemDef.getId());
        String name = rl.getPath();

        Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_MISC);
        if(itemDef.getMaxStackSize() != 64) props.stacksTo(itemDef.getMaxStackSize());

        ITEMS.register(name, () -> new Item(props));
    }

    @Override
    public void registerBlockItem(CoreBlockItemDef blockItemDef)
    {
        CoreBlockDef blockDef = blockItemDef.getBlock();
        ResourceLocation rl = toRL(blockDef.getId());
        String name = rl.getPath();

        // Register block first
        RegistryObject<Block> regBlock = BLOCKS.register(name, () -> createBlockFromProps(blockDef));

        // If the combined def includes an item, register a BlockItem that points to the registered block.
        if(blockItemDef.hasItem())
        {
            CoreItemDef itemDef = blockItemDef.getItem();
            Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS);
            if(itemDef.getMaxStackSize() != 64) props.stacksTo(itemDef.getMaxStackSize());

            ITEMS.register(name, () -> new BlockItem(regBlock.get(), props));
        }
    }

    @Override
    public void registerMenus()
    {

    }

    private ResourceLocation toRL(Identifier id)
    {
        return CraftCreator.getInstance().getPlatform().getIdentifierAdapter().fromCore(id);
    }

    private Block createBlockFromProps(CoreBlockDef def)
    {
        FacingType facing = def.getFacingType();

        BlockBehaviour.Properties props = BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.STONE);
        // parse other def.getProperties() into props as needed

        if(!def.isRotateModel()) return new Block(props);

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
                    CoreVoxelShape coreShape = def.getFacingShapes().getOrDefault(CraftCreator.getInstance().getPlatform().getFacingAdapter().toCore(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)), CoreShapes.FULL);

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

                @Override
                public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
                {
                    if(world.isClientSide())
                    {
                        // Client: return success to swing the hand etc. Server will open UI.
                        return InteractionResult.SUCCESS;
                    }

                    // Server side: open container
                    if(player instanceof ServerPlayer serverPlayer)
                    {

                        // Create a MenuProvider that knows how to create the server-side menu
                        MenuProvider provider = new MenuProvider()
                        {
                            @Override
                            public Component getDisplayName()
                            {
                                return new TranslatableComponent("container.craftcreator.recipe_editor"); // i18n key
                            }

                            @Override
                            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player p)
                            {
                                // Construct server-side menu using the ContainerModel from core or from block entity.
                                // Here we create a fresh model or fetch it from the BE at pos if you have one.
                                CraftingTableRecipeCreatorContainerModel model = new CraftingTableRecipeCreatorContainerModel();
                                return new ForgeRecipeCreatorMenu(windowId, playerInventory, model);
                            }
                        };

                        // Use NetworkHooks to open the screen; write position so client can recreate menu/lookup BE
                        NetworkHooks.openGui(serverPlayer, provider, buf ->
                        {
                            buf.writeBlockPos(pos);
                            // write extra data if needed (e.g. a registry id)
                        });

                        return InteractionResult.CONSUME;
                    }

                    return InteractionResult.PASS;
                }
            };
            default -> new Block(props);
        };
    }
}