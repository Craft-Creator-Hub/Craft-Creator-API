package fr.en0ri4n.craftcreator.platform.blockentity;

import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityContext;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Forge implementation of BlockEntityContext.
 * Wraps Level/BlockPos and optionally an interacting ServerPlayer.
 */
public class ForgeBlockEntityContext implements BlockEntityContext {

    private final Level level;
    private final BlockPos pos;
    private final ServerPlayer interacting; // may be null

    public ForgeBlockEntityContext(Level level, BlockPos pos, ServerPlayer interacting) {
        this.level = level;
        this.pos = pos;
        this.interacting = interacting;
    }

    @Override
    public boolean isClient() {
        return level.isClientSide();
    }

    @Override
    public String getPosAsString() {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    @Override
    public void markDirty() {
        if (level != null) level.blockUpdated(pos, level.getBlockState(pos).getBlock());
    }

    @Override
    public void sendBlockUpdate() {
        if (level != null) level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
    }

    @Override
    public void openContainer(Identifier containerId) {
        // implement as needed — contexts can invoke NetworkHooks.openScreen from block / BE code
    }

    @Override
    public Optional<String> getInteractingPlayer() {
        return interacting == null ? Optional.empty() : Optional.of(interacting.getGameProfile().getName());
    }
}