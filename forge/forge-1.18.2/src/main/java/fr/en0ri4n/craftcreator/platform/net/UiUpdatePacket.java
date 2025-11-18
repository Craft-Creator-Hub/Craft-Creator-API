package fr.en0ri4n.craftcreator.platform.net;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class UiUpdatePacket
{
    private final UiUpdateData data;

    public UiUpdatePacket(UiUpdateData data)
    {
        this.data = data;
    }

    public UiUpdateData getData()
    {
        return data;
    }

    public static void encode(UiUpdatePacket pkt, FriendlyByteBuf buf)
    {
        buf.writeUtf(pkt.getData().getContainerId().toString());
        buf.writeUtf(pkt.getData().getPayload().toString());
    }

    public static UiUpdatePacket decode(FriendlyByteBuf buf)
    {
        String containerIdStr = buf.readUtf();
        String payloadJson = buf.readUtf();

        JsonObject payload = new JsonObject();
        try
        {
            JsonElement el = JsonParser.parseString(payloadJson);
            if(el.isJsonObject()) payload = el.getAsJsonObject();
        }
        catch(Exception ignored)
        {
            CraftCreator.LOGGER.error("UiUpdatePacket: failed to parse payload JSON: {}", payloadJson);
        }

        UiUpdateData updateData = new UiUpdateData(Identifier.from(containerIdStr), payload);
        return new UiUpdatePacket(updateData);
    }

    public static void clientHandle(UiUpdatePacket pkt, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> Forge1182Platform.get().getNetworkInteractionAdapter().handleClientDataUpdate(pkt.getData()));
        ctx.setPacketHandled(true);
    }
}