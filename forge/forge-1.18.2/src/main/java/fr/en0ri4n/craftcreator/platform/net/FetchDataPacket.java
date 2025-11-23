package fr.en0ri4n.craftcreator.platform.net;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
public class FetchDataPacket
{
    private final FetchData data;

    public FetchDataPacket(FetchData data)
    {
        this.data = data;
    }

    public static void encode(FetchDataPacket packet, FriendlyByteBuf buf)
    {
        buf.writeBlockPos(Forge1182Platform.get().getBlockPosAdapter().fromCore(packet.getData().getPos()));
        buf.writeUtf(packet.getData().getContainerId().toString());
    }

    public static FetchDataPacket decode(FriendlyByteBuf buf)
    {
        CoreBlockPos pos = Forge1182Platform.get().getBlockPosAdapter().toCore(buf.readBlockPos());
        Identifier containerId = Identifier.from(buf.readUtf());

        FetchData data = new FetchData(pos, containerId);
        return new FetchDataPacket(data);
    }

    public static void handleServer(FetchDataPacket packet, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> Forge1182Platform.get().getNetworkInteractionAdapter().handleServerFetchData(ctx.getSender(), packet.getData()));
        ctx.setPacketHandled(true);
    }
}
