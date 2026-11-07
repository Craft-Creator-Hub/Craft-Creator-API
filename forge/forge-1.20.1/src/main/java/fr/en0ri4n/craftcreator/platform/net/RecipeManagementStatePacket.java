package fr.en0ri4n.craftcreator.platform.net;

import fr.en0ri4n.craftcreator.api.net.RecipeManagementState;
import fr.en0ri4n.craftcreator.platform.Forge1201Platform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * Carries the exporter's recipes as json, so it can be large: written as a byte array instead of a size-limited string.
 */
public record RecipeManagementStatePacket(RecipeManagementState state)
{
    public static void encode(RecipeManagementStatePacket packet, FriendlyByteBuf buf)
    {
        buf.writeByteArray(packet.state().toJsonString().getBytes(StandardCharsets.UTF_8));
    }

    public static RecipeManagementStatePacket decode(FriendlyByteBuf buf)
    {
        return new RecipeManagementStatePacket(RecipeManagementState.fromJsonString(new String(buf.readByteArray(), StandardCharsets.UTF_8)));
    }

    public static void handleClient(RecipeManagementStatePacket packet, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> Forge1201Platform.get().getNetworkInteractionAdapter().handleClientManagementState(packet.state()));
        ctx.setPacketHandled(true);
    }
}
