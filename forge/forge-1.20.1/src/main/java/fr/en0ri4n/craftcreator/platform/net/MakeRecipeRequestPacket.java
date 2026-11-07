package fr.en0ri4n.craftcreator.platform.net;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.platform.Forge1201Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
public class MakeRecipeRequestPacket
{
    private final MakeRecipeRequestData data;

    public MakeRecipeRequestPacket(MakeRecipeRequestData data)
    {
        this.data = data;
    }

    public static void encode(MakeRecipeRequestPacket packet, FriendlyByteBuf buf)
    {
        buf.writeBlockPos(fr.en0ri4n.craftcreator.platform.Forge1201Platform.get().getBlockPosAdapter().fromCore(packet.getData().getPos()));
        buf.writeUtf(packet.getData().getContainerId().toString());
        buf.writeBoolean(packet.getData().isPreview());
    }

    public static MakeRecipeRequestPacket decode(FriendlyByteBuf buf)
    {
        CoreBlockPos pos = Forge1201Platform.get().getBlockPosAdapter().toCore(buf.readBlockPos());
        Identifier containerId = Identifier.from(buf.readUtf());
        boolean preview = buf.readBoolean();

        MakeRecipeRequestData data = new MakeRecipeRequestData(pos, containerId, preview);
        return new MakeRecipeRequestPacket(data);
    }

    public static void handleServer(MakeRecipeRequestPacket packet, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> Forge1201Platform.get().getNetworkInteractionAdapter().handleServerMakeRecipeRequest(ctx.getSender(), packet.getData()));
        ctx.setPacketHandled(true);
    }
}
