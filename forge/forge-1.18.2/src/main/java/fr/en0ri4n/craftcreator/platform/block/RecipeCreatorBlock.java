package fr.en0ri4n.craftcreator.platform.block;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Recipe Creator Block that opens a 10-slot container with a dropdown for recipe type selection.
 */
public class RecipeCreatorBlock extends Block implements EntityBlock {

    public RecipeCreatorBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .strength(3.0f, 3.0f)
                .requiresCorrectToolForDrops());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Create a ForgeGenericBlockEntity and set its core entity
        ForgeGenericBlockEntity blockEntity = new ForgeGenericBlockEntity(pos, state);
        
        // Create a core entity for the recipe_creator type
        CoreBlockEntity coreEntity = CoreBlockEntityManager.get().create(Identifier.from("craftcreator:recipe_creator"));
        
        // Set the container ID in extraData so onInteract knows which menu to open
        coreEntity.getExtraData().addProperty("container", "craftcreator:recipe_creator");
        
        // Assign the core entity to the forge block entity
        blockEntity.setCoreEntity(coreEntity);
        
        return blockEntity;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide()) {
            // Client: return success to swing the hand etc. Server will open UI.
            return InteractionResult.SUCCESS;
        }

        // Server side: forward to block entity's onInteract
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ForgeGenericBlockEntity && player instanceof ServerPlayer) {
            ForgeGenericBlockEntity genericBE = (ForgeGenericBlockEntity) be;
            boolean handled = genericBE.onInteract((ServerPlayer) player);
            return handled ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }
}
