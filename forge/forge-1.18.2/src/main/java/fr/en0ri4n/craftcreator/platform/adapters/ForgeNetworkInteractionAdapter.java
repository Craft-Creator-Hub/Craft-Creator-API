package fr.en0ri4n.craftcreator.platform.adapters;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.*;
import fr.en0ri4n.craftcreator.api.platform.NetworkInteractionAdapter;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.net.*;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeRecipeCreatorScreen;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.recipe.RecipeManager;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
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

    @Override
    public void sendOpenContainerRequestToServer(OpenContainerRequestData request)
    {
        NetworkHandler.INSTANCE.sendToServer(new OpenContainerRequestPacket(request));
    }

    @Override
    public void handleServerOpenContainerRequest(ServerPlayer sender, OpenContainerRequestData request)
    {
        ServerLevel level = sender.getLevel();
        BlockPos pos = Forge1182Platform.get().getBlockPosAdapter().fromCore(request.getBlockPos());
        ForgeGenericBlockEntity blockEntity = (ForgeGenericBlockEntity) level.getBlockEntity(pos);

        // Use NetworkHooks to open the screen; write position so client can recreate menu/lookup BE
        NetworkHooks.openGui(sender, blockEntity, buf ->
        {
            buf.writeBlockPos(pos);
            buf.writeUtf(request.getContainerId().toString());
        });
    }

    @Override
    public void sendMakeRecipeRequestToServer(MakeRecipeRequestData data)
    {
        NetworkHandler.INSTANCE.sendToServer(new MakeRecipeRequestPacket(data));
    }

    @Override
    public void handleServerMakeRecipeRequest(ServerPlayer player, MakeRecipeRequestData data)
    {
        ServerLevel level = player.getLevel();
        BlockPos pos = Forge1182Platform.get().getBlockPosAdapter().fromCore(data.getPos());
        ForgeGenericBlockEntity blockEntity = (ForgeGenericBlockEntity) level.getBlockEntity(pos);

        if(blockEntity == null)
            return;

        RecipeRequestFeedback feedback = RecipeManager.get().handleMakeRecipeRequest(blockEntity.getCoreEntity(), data.getContainerId());
        player.sendMessage(new TextComponent(feedback.getFeedback().getMessageKey()), player.getUUID());
    }
}
