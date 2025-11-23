package fr.en0ri4n.craftcreator.platform.net;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
public class BlockEntityUpdatePacket
{
    private final BlockEntityUpdateData data;

    public BlockEntityUpdatePacket(BlockEntityUpdateData data) {
        this.data = data;
    }

    public static void encode(BlockEntityUpdatePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(Forge1182Platform.get().getBlockPosAdapter().fromCore(packet.getData().getPos()));
        buf.writeUtf(packet.getData().getContainerId().toString());
        buf.writeUtf(packet.getData().getPayload().toString());
    }

    public static BlockEntityUpdatePacket decode(FriendlyByteBuf buf) {
        CoreBlockPos pos = Forge1182Platform.get().getBlockPosAdapter().toCore(buf.readBlockPos());
        String containerIdStr = buf.readUtf();
        String payloadJson = buf.readUtf();

        JsonObject payload = new JsonObject();
        try {
            JsonElement el = JsonParser.parseString(payloadJson);
            if(el.isJsonObject()) payload = el.getAsJsonObject();
        }
        catch(JsonSyntaxException e)
        {
            CraftCreator.LOGGER.error("Failed to parse BlockEntityUpdatePacket payload JSON: {}", payloadJson, e);
        }

        BlockEntityUpdateData updateData = new BlockEntityUpdateData(pos, Identifier.from(containerIdStr), payload);
        return new BlockEntityUpdatePacket(updateData);
    }

    public static void handleServer(BlockEntityUpdatePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> Forge1182Platform.get().getNetworkInteractionAdapter().handleServerDataUpdate(ctx.getSender(), packet.getData()));
        ctx.setPacketHandled(true);
    }
}
