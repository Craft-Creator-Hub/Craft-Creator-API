package fr.en0ri4n.craftcreator.platform.net;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
import fr.en0ri4n.craftcreator.platform.NeoForge1206Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;


@Getter
public class OpenContainerRequestPacket implements CustomPacketPayload
{
    public static final Type<OpenContainerRequestPacket> TYPE = new Type<>(new ResourceLocation(CCReferences.MOD_ID, "open_container_request"));
    public static final StreamCodec<FriendlyByteBuf, OpenContainerRequestPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), OpenContainerRequestPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private final OpenContainerRequestData data;

    public OpenContainerRequestPacket(OpenContainerRequestData data)
    {
        this.data = data;
    }

    public static void encode(OpenContainerRequestPacket packet, FriendlyByteBuf buf)
    {
        buf.writeBlockPos(NeoForge1206Platform.get().getBlockPosAdapter().fromCore(packet.getData().getBlockPos()));
        buf.writeUtf(packet.getData().getContainerId().toString());
    }

    public static OpenContainerRequestPacket decode(FriendlyByteBuf buf)
    {
        CoreBlockPos pos = NeoForge1206Platform.get().getBlockPosAdapter().toCore(buf.readBlockPos());
        String containerIdStr = buf.readUtf();

        OpenContainerRequestData openContainerRequestData = new OpenContainerRequestData(pos, Identifier.from(containerIdStr));
        return new OpenContainerRequestPacket(openContainerRequestData);
    }

    public static void handleServer(OpenContainerRequestPacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> NeoForge1206Platform.get().getNetworkInteractionAdapter().handleServerOpenContainerRequest((ServerPlayer) ctx.player(), packet.getData()));
    }
}
