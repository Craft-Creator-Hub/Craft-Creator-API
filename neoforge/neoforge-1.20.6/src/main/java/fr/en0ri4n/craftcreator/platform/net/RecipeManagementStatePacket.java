package fr.en0ri4n.craftcreator.platform.net;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementState;
import fr.en0ri4n.craftcreator.platform.NeoForge1206Platform;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * Carries the exporter's recipes as json, so it can be large: written as a byte array instead of a size-limited string.
 */
public record RecipeManagementStatePacket(RecipeManagementState state) implements CustomPacketPayload
{
    public static final Type<RecipeManagementStatePacket> TYPE = new Type<>(new ResourceLocation(CCReferences.MOD_ID, "recipe_management_state"));
    public static final StreamCodec<FriendlyByteBuf, RecipeManagementStatePacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), RecipeManagementStatePacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void encode(RecipeManagementStatePacket packet, FriendlyByteBuf buf)
    {
        buf.writeByteArray(packet.state().toJsonString().getBytes(StandardCharsets.UTF_8));
    }

    public static RecipeManagementStatePacket decode(FriendlyByteBuf buf)
    {
        return new RecipeManagementStatePacket(RecipeManagementState.fromJsonString(new String(buf.readByteArray(), StandardCharsets.UTF_8)));
    }

    public static void handleClient(RecipeManagementStatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> NeoForge1206Platform.get().getNetworkInteractionAdapter().handleClientManagementState(packet.state()));
    }
}
