package fr.en0ri4n.craftcreator.platform.net;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.platform.NeoForge1211Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;


@Getter
public class MakeRecipeRequestPacket implements CustomPacketPayload
{
    public static final Type<MakeRecipeRequestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CCReferences.MOD_ID, "make_recipe_request"));
    public static final StreamCodec<FriendlyByteBuf, MakeRecipeRequestPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), MakeRecipeRequestPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private final MakeRecipeRequestData data;

    public MakeRecipeRequestPacket(MakeRecipeRequestData data)
    {
        this.data = data;
    }

    public static void encode(MakeRecipeRequestPacket packet, FriendlyByteBuf buf)
    {
        buf.writeBlockPos(fr.en0ri4n.craftcreator.platform.NeoForge1211Platform.get().getBlockPosAdapter().fromCore(packet.getData().getPos()));
        buf.writeUtf(packet.getData().getContainerId().toString());
        buf.writeBoolean(packet.getData().isPreview());
    }

    public static MakeRecipeRequestPacket decode(FriendlyByteBuf buf)
    {
        CoreBlockPos pos = NeoForge1211Platform.get().getBlockPosAdapter().toCore(buf.readBlockPos());
        Identifier containerId = Identifier.from(buf.readUtf());
        boolean preview = buf.readBoolean();

        MakeRecipeRequestData data = new MakeRecipeRequestData(pos, containerId, preview);
        return new MakeRecipeRequestPacket(data);
    }

    public static void handleServer(MakeRecipeRequestPacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> NeoForge1211Platform.get().getNetworkInteractionAdapter().handleServerMakeRecipeRequest((ServerPlayer) ctx.player(), packet.getData()));
    }
}
