package fr.en0ri4n.craftcreator.platform.net;

import fr.en0ri4n.craftcreator.api.net.RecipeManagementRequest;
import fr.en0ri4n.craftcreator.platform.Forge1201Platform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RecipeManagementRequestPacket(RecipeManagementRequest request)
{
    public static void encode(RecipeManagementRequestPacket packet, FriendlyByteBuf buf)
    {
        buf.writeUtf(packet.request().toJsonString(), Short.MAX_VALUE);
    }

    public static RecipeManagementRequestPacket decode(FriendlyByteBuf buf)
    {
        return new RecipeManagementRequestPacket(RecipeManagementRequest.fromJsonString(buf.readUtf(Short.MAX_VALUE)));
    }

    public static void handleServer(RecipeManagementRequestPacket packet, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> Forge1201Platform.get().getNetworkInteractionAdapter().handleServerManagementRequest(ctx.getSender(), packet.request()));
        ctx.setPacketHandled(true);
    }
}
