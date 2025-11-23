package fr.en0ri4n.craftcreator.platform.net;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
public class OpenContainerRequestPacket
{
    private final OpenContainerRequestData data;

    public OpenContainerRequestPacket(OpenContainerRequestData data)
    {
        this.data = data;
    }

    public static void encode(OpenContainerRequestPacket packet, FriendlyByteBuf buf)
    {
        buf.writeBlockPos(Forge1182Platform.get().getBlockPosAdapter().fromCore(packet.getData().getBlockPos()));
        buf.writeUtf(packet.getData().getContainerId().toString());
    }

    public static OpenContainerRequestPacket decode(FriendlyByteBuf buf)
    {
        CoreBlockPos pos = Forge1182Platform.get().getBlockPosAdapter().toCore(buf.readBlockPos());
        String containerIdStr = buf.readUtf();

        OpenContainerRequestData openContainerRequestData = new OpenContainerRequestData(pos, Identifier.from(containerIdStr));
        return new OpenContainerRequestPacket(openContainerRequestData);
    }

    public static void handleServer(OpenContainerRequestPacket packet, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> Forge1182Platform.get().getNetworkInteractionAdapter().handleServerOpenContainerRequest(ctx.getSender(), packet.getData()));
        ctx.setPacketHandled(true);
    }
}
