package fr.en0ri4n.craftcreator.platform.adapters;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.platform.NetworkInteractionAdapter;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.net.BlockEntityUpdatePacket;
import fr.en0ri4n.craftcreator.platform.net.FetchDataPacket;
import fr.en0ri4n.craftcreator.platform.net.NetworkHandler;
import fr.en0ri4n.craftcreator.platform.net.UiUpdatePacket;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorScreen;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;

public class ForgeNetworkInteractionAdapter implements NetworkInteractionAdapter<ServerPlayer>
{
    @Override
    public void sendDataUpdateToClient(ServerPlayer sender, UiUpdateData data)
    {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new UiUpdatePacket(data));
    }

    @Override
    public void sendDataUpdateToServer(BlockEntityUpdateData data)
    {
        NetworkHandler.INSTANCE.sendToServer(new BlockEntityUpdatePacket(data));
    }

    @Override
    public void handleClientDataUpdate(UiUpdateData data)
    {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.screen instanceof ForgeRecipeCreatorScreen gui)
            {
                gui.getModel().getScreenDefinition().updateScreen(data);
            }
        });
    }

    @Override
    public void fetchData(FetchData data)
    {
        NetworkHandler.INSTANCE.sendToServer(new FetchDataPacket(data));
    }

    @Override
    public void handleServerFetchData(ServerPlayer sender, FetchData data)
    {
        ForgeGenericBlockEntity gbe = getBlockEntity(sender, data.getPos(), data.getContainerId());

        if(gbe == null)
            return;

        JsonObject payload = gbe.getCoreEntity().fetchBehaviorData();

        // send data update to client
        UiUpdateData updateData = new UiUpdateData(data.getContainerId(), payload);
        sendDataUpdateToClient(sender, updateData);
    }

    @Override
    public void handleServerDataUpdate(ServerPlayer sender, BlockEntityUpdateData data)
    {
        ForgeGenericBlockEntity gbe = getBlockEntity(sender, data.getPos(), data.getContainerId());

        if(gbe == null)
            return;

        gbe.getCoreEntity().updateBehaviorData(data.getPayload());

        // mark changed and request sync to clients
        gbe.setChanged();

        // send block update to trigger client BE sync
        sender.level.sendBlockUpdated(gbe.getBlockPos(), sender.level.getBlockState(gbe.getBlockPos()), sender.level.getBlockState(gbe.getBlockPos()), 3);
    }

    private ForgeGenericBlockEntity getBlockEntity(ServerPlayer sender, CoreBlockPos pos, Identifier containerId)
    {
        if(sender == null) // no sender -> ignore
            return null;

        BlockPos blockPos = Forge1182Platform.get().getBlockPosAdapter().fromCore(pos);
        Level level = sender.level;

        BlockEntity be = level.getBlockEntity(blockPos);
        if(!(be instanceof ForgeGenericBlockEntity gbe))
            return null;

        CoreBlockEntity core = gbe.getCoreEntity();
        if(core == null)
            return null;

        // Validate container id
        if(!containerId.toString().equals(core.getTypeId().toString()))
        {
            CraftCreator.LOGGER.error("PacketUiUpdate: invalid container id {} expected {} for block entity at {}", containerId, core.getTypeId(), pos);
            return null;
        }

        return gbe;
    }
}
