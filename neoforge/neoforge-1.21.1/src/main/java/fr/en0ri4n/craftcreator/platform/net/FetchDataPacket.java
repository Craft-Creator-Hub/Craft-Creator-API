package fr.en0ri4n.craftcreator.platform.net;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.platform.NeoForge1211Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;


@Getter
public class FetchDataPacket implements CustomPacketPayload
{
    public static final Type<FetchDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CCReferences.MOD_ID, "fetch_data"));
    public static final StreamCodec<FriendlyByteBuf, FetchDataPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), FetchDataPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private final FetchData data;

    public FetchDataPacket(FetchData data)
    {
        this.data = data;
    }

    public static void encode(FetchDataPacket packet, FriendlyByteBuf buf)
    {
        buf.writeBlockPos(NeoForge1211Platform.get().getBlockPosAdapter().fromCore(packet.getData().getPos()));
        buf.writeUtf(packet.getData().getContainerId().toString());
    }

    public static FetchDataPacket decode(FriendlyByteBuf buf)
    {
        CoreBlockPos pos = NeoForge1211Platform.get().getBlockPosAdapter().toCore(buf.readBlockPos());
        Identifier containerId = Identifier.from(buf.readUtf());

        FetchData data = new FetchData(pos, containerId);
        return new FetchDataPacket(data);
    }

    public static void handleServer(FetchDataPacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> NeoForge1211Platform.get().getNetworkInteractionAdapter().handleServerFetchData((ServerPlayer) ctx.player(), packet.getData()));
    }
}
