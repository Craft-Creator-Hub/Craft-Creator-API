package fr.en0ri4n.craftcreator.platform.block;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreShapes;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreVoxelShape;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Block class for the every recipe creator blocks.
 */
public class RecipeCreatorBlock extends Block implements EntityBlock
{
    private final CoreBlockDef coreBlockDef;

    public RecipeCreatorBlock(CoreBlockDef coreBlockId)
    {
        super(BlockBehaviour.Properties.of(Material.METAL).strength(3.0f, 3.0f).requiresCorrectToolForDrops());
        this.coreBlockDef = coreBlockId;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new ForgeGenericBlockEntity(pos, state, coreBlockDef.getId());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState)
    {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        CoreVoxelShape coreShape = coreBlockDef.getFacingShapes().getOrDefault(Forge1182Platform.get().getFacingAdapter().toCore(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)), CoreShapes.FULL);

        return Forge1182Platform.get().getBlockShapeAdapter().toPlatformShape(coreShape);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if(world.isClientSide()) return InteractionResult.SUCCESS;

        if(player instanceof ServerPlayer serverPlayer)
        {
            OpenContainerRequestData request = new OpenContainerRequestData(Forge1182Platform.get().getBlockPosAdapter().toCore(pos), coreBlockDef.getId());
            Forge1182Platform.get().getNetworkInteractionAdapter().handleServerOpenContainerRequest(serverPlayer, request);

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
