package fr.en0ri4n.craftcreator.platform.net;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import fr.en0ri4n.craftcreator.api.CCReferences;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.platform.NeoForge1206Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;


@Getter
public class UiUpdatePacket implements CustomPacketPayload
{
    public static final Type<UiUpdatePacket> TYPE = new Type<>(new ResourceLocation(CCReferences.MOD_ID, "ui_update"));
    public static final StreamCodec<FriendlyByteBuf, UiUpdatePacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), UiUpdatePacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private final UiUpdateData data;

    public UiUpdatePacket(UiUpdateData data)
    {
        this.data = data;
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

    public static void clientHandle(UiUpdatePacket pkt, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> NeoForge1206Platform.get().getNetworkInteractionAdapter().handleClientDataUpdate(pkt.getData()));
    }
}