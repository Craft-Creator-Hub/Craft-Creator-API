package fr.en0ri4n.craftcreator.platform.net;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementRequest;
import fr.en0ri4n.craftcreator.platform.NeoForge1211Platform;
import net.minecraft.network.FriendlyByteBuf;


public record RecipeManagementRequestPacket(RecipeManagementRequest request) implements CustomPacketPayload
{
    public static final Type<RecipeManagementRequestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CCReferences.MOD_ID, "recipe_management_request"));
    public static final StreamCodec<FriendlyByteBuf, RecipeManagementRequestPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), RecipeManagementRequestPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void encode(RecipeManagementRequestPacket packet, FriendlyByteBuf buf)
    {
        buf.writeUtf(packet.request().toJsonString(), Short.MAX_VALUE);
    }

    public static RecipeManagementRequestPacket decode(FriendlyByteBuf buf)
    {
        return new RecipeManagementRequestPacket(RecipeManagementRequest.fromJsonString(buf.readUtf(Short.MAX_VALUE)));
    }

    public static void handleServer(RecipeManagementRequestPacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> NeoForge1211Platform.get().getNetworkInteractionAdapter().handleServerManagementRequest((ServerPlayer) ctx.player(), packet.request()));
    }
}
