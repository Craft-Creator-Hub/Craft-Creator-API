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
import com.google.gson.JsonSyntaxException;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.platform.NeoForge1211Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;


@Getter
public class BlockEntityUpdatePacket implements CustomPacketPayload
{
    public static final Type<BlockEntityUpdatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CCReferences.MOD_ID, "block_entity_update"));
    public static final StreamCodec<FriendlyByteBuf, BlockEntityUpdatePacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), BlockEntityUpdatePacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private final BlockEntityUpdateData data;

    public BlockEntityUpdatePacket(BlockEntityUpdateData data) {
        this.data = data;
    }

    public static void encode(BlockEntityUpdatePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(NeoForge1211Platform.get().getBlockPosAdapter().fromCore(packet.getData().getPos()));
        buf.writeUtf(packet.getData().getContainerId().toString());
        buf.writeUtf(packet.getData().getPayload().toString());
    }

    public static BlockEntityUpdatePacket decode(FriendlyByteBuf buf) {
        CoreBlockPos pos = NeoForge1211Platform.get().getBlockPosAdapter().toCore(buf.readBlockPos());
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

    public static void handleServer(BlockEntityUpdatePacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> NeoForge1211Platform.get().getNetworkInteractionAdapter().handleServerDataUpdate((ServerPlayer) ctx.player(), packet.getData()));
    }
}
