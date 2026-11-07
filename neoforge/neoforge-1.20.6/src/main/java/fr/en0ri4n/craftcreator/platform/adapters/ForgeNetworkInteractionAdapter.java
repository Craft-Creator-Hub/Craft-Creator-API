package fr.en0ri4n.craftcreator.platform.adapters;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementRequest;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementState;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeManagementListener;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.platform.NetworkInteractionAdapter;
import fr.en0ri4n.craftcreator.platform.NeoForge1206Platform;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.net.BlockEntityUpdatePacket;
import fr.en0ri4n.craftcreator.platform.net.FetchDataPacket;
import fr.en0ri4n.craftcreator.platform.net.MakeRecipeRequestPacket;
import fr.en0ri4n.craftcreator.platform.net.NetworkHandler;
import fr.en0ri4n.craftcreator.platform.net.OpenContainerRequestPacket;
import fr.en0ri4n.craftcreator.platform.net.RecipeManagementRequestPacket;
import fr.en0ri4n.craftcreator.platform.net.RecipeFeedbackPacket;
import fr.en0ri4n.craftcreator.platform.net.RecipeManagementStatePacket;
import fr.en0ri4n.craftcreator.platform.net.UiUpdatePacket;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeRecipeCreatorScreen;
import fr.en0ri4n.craftcreator.platform.ui.screen.ForgeScreen;
import fr.en0ri4n.craftcreator.recipe.RecipeManagementService;
import fr.en0ri4n.craftcreator.utils.Feedback;
import fr.en0ri4n.craftcreator.recipe.RecipeManager;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeFeedbackDisplay;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;

import java.util.List;

public class ForgeNetworkInteractionAdapter implements NetworkInteractionAdapter<ServerPlayer>
{
    @Override
    public void sendDataUpdateToClient(ServerPlayer sender, UiUpdateData data)
    {
        NetworkHandler.sendToPlayer(sender, new UiUpdatePacket(data));
    }

    @Override
    public void sendDataUpdateToServer(BlockEntityUpdateData data)
    {
        NetworkHandler.sendToServer(new BlockEntityUpdatePacket(data));
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
        NetworkHandler.sendToServer(new FetchDataPacket(data));
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
        sender.level().sendBlockUpdated(gbe.getBlockPos(), sender.level().getBlockState(gbe.getBlockPos()), sender.level().getBlockState(gbe.getBlockPos()), 3);
    }

    private ForgeGenericBlockEntity getBlockEntity(ServerPlayer sender, CoreBlockPos pos, Identifier containerId)
    {
        if(sender == null) // no sender -> ignore
            return null;

        BlockPos blockPos = NeoForge1206Platform.get().getBlockPosAdapter().fromCore(pos);
        Level level = sender.level();

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
        NetworkHandler.sendToServer(new OpenContainerRequestPacket(request));
    }

    @Override
    public void handleServerOpenContainerRequest(ServerPlayer sender, OpenContainerRequestData request)
    {
        ServerLevel level = sender.serverLevel();
        BlockPos pos = NeoForge1206Platform.get().getBlockPosAdapter().fromCore(request.getBlockPos());
        ForgeGenericBlockEntity blockEntity = (ForgeGenericBlockEntity) level.getBlockEntity(pos);

        // Write the position so the client can recreate the menu and find the block entity
        sender.openMenu(blockEntity, buf ->
        {
            buf.writeBlockPos(pos);
            buf.writeUtf(request.getContainerId().toString());
        });
    }

    @Override
    public void sendMakeRecipeRequestToServer(MakeRecipeRequestData data)
    {
        NetworkHandler.sendToServer(new MakeRecipeRequestPacket(data));
    }

    @Override
    public void handleServerMakeRecipeRequest(ServerPlayer player, MakeRecipeRequestData data)
    {
        ServerLevel level = player.serverLevel();
        BlockPos pos = NeoForge1206Platform.get().getBlockPosAdapter().fromCore(data.getPos());
        ForgeGenericBlockEntity blockEntity = (ForgeGenericBlockEntity) level.getBlockEntity(pos);

        if(blockEntity == null)
            return;

        RecipeRequestFeedback requestFeedback = RecipeManager.get().handleMakeRecipeRequest(blockEntity.getCoreEntity(), data.getContainerId(), data.isPreview());

        // The open screen shows the message (or the json preview); an exported recipe also gets its chat message, with its file and json links
        NetworkHandler.sendToPlayer(player, new RecipeFeedbackPacket(requestFeedback));
        if(requestFeedback.isSuccess() && !data.isPreview())
            player.sendSystemMessage(NeoForge1206Platform.get().getTextComponentProvider().createFeedbackComponent(requestFeedback));
    }

    @Override
    public void handleClientRecipeFeedback(RecipeRequestFeedback feedback)
    {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            Minecraft mc = Minecraft.getInstance();
            CoreScreenDefinition<?> screen = null;
            if(mc.screen instanceof ForgeScreen forgeScreen)
                screen = forgeScreen.getScreenDefinition();
            else if(mc.screen instanceof ForgeRecipeCreatorScreen recipeCreatorScreen)
                screen = recipeCreatorScreen.getModel().getScreenDefinition();

            // No Craft Creator screen open anymore: errors go to the chat (successes are already there)
            if(!RecipeFeedbackDisplay.show(screen, feedback) && !feedback.isSuccess() && mc.player != null)
                mc.player.displayClientMessage(Component.literal(RecipeFeedbackDisplay.message(feedback)), false);
        });
    }

    @Override
    public void sendManagementRequestToServer(RecipeManagementRequest request)
    {
        NetworkHandler.sendToServer(new RecipeManagementRequestPacket(request));
    }

    @Override
    public void handleServerManagementRequest(ServerPlayer player, RecipeManagementRequest request)
    {
        if(player == null) return;

        // Editing recipes writes into the server's datapacks and scripts: operators only
        RecipeManagementState state = player.hasPermissions(2)
                ? RecipeManagementService.get().handle(request)
                : new RecipeManagementState(request.getExporter(), List.of(), List.of(), List.of(), false, Feedback.NO_PERMISSION, false);

        NetworkHandler.sendToPlayer(player, new RecipeManagementStatePacket(state));
    }

    @Override
    public void handleClientManagementState(RecipeManagementState state)
    {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            if(Minecraft.getInstance().screen instanceof ForgeScreen screen && screen.getScreenDefinition() instanceof RecipeManagementListener listener)
                listener.onManagementState(state);
        });
    }
}
