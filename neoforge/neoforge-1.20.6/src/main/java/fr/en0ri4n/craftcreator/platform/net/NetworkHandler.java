package fr.en0ri4n.craftcreator.platform.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Craft Creator's payloads: client to server requests and server to client answers.
 */
public final class NetworkHandler
{
    private static final String PROTOCOL = "2";

    private NetworkHandler() {}

    /**
     * Called on the mod event bus.
     */
    public static void register(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar(PROTOCOL);

        // Client -> server
        registrar.playToServer(BlockEntityUpdatePacket.TYPE, BlockEntityUpdatePacket.STREAM_CODEC, BlockEntityUpdatePacket::handleServer);
        registrar.playToServer(FetchDataPacket.TYPE, FetchDataPacket.STREAM_CODEC, FetchDataPacket::handleServer);
        registrar.playToServer(OpenContainerRequestPacket.TYPE, OpenContainerRequestPacket.STREAM_CODEC, OpenContainerRequestPacket::handleServer);
        registrar.playToServer(MakeRecipeRequestPacket.TYPE, MakeRecipeRequestPacket.STREAM_CODEC, MakeRecipeRequestPacket::handleServer);
        registrar.playToServer(RecipeManagementRequestPacket.TYPE, RecipeManagementRequestPacket.STREAM_CODEC, RecipeManagementRequestPacket::handleServer);

        // Server -> client
        registrar.playToClient(UiUpdatePacket.TYPE, UiUpdatePacket.STREAM_CODEC, UiUpdatePacket::clientHandle);
        registrar.playToClient(RecipeManagementStatePacket.TYPE, RecipeManagementStatePacket.STREAM_CODEC, RecipeManagementStatePacket::handleClient);
        registrar.playToClient(RecipeFeedbackPacket.TYPE, RecipeFeedbackPacket.STREAM_CODEC, RecipeFeedbackPacket::handleClient);
    }

    public static void sendToServer(CustomPacketPayload payload)
    {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
