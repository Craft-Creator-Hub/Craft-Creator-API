package fr.en0ri4n.craftcreator.platform.net;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.platform.NeoForge1211Platform;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.utils.Feedback;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * Server to client: the answer to a recipe request (success or error message, and the recipe's json for a preview).
 * The json is written as a byte array, as it can be longer than a size-limited string.
 */
public record RecipeFeedbackPacket(RecipeRequestFeedback feedback) implements CustomPacketPayload
{
    public static final Type<RecipeFeedbackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CCReferences.MOD_ID, "recipe_feedback"));
    public static final StreamCodec<FriendlyByteBuf, RecipeFeedbackPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), RecipeFeedbackPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void encode(RecipeFeedbackPacket packet, FriendlyByteBuf buf)
    {
        RecipeRequestFeedback feedback = packet.feedback();
        buf.writeEnum(feedback.getFeedback());
        buf.writeBoolean(feedback.isSuccess());
        buf.writeBoolean(feedback.getRecipeType() != null);
        if(feedback.getRecipeType() != null) buf.writeEnum(feedback.getRecipeType());
        buf.writeUtf(feedback.getRecipeName() != null ? feedback.getRecipeName() : "");
        buf.writeUtf(feedback.getRecipePath() != null ? feedback.getRecipePath() : "");
        buf.writeByteArray((feedback.getRecipeJson() != null ? feedback.getRecipeJson() : "").getBytes(StandardCharsets.UTF_8));
    }

    public static RecipeFeedbackPacket decode(FriendlyByteBuf buf)
    {
        Feedback feedback = buf.readEnum(Feedback.class);
        boolean success = buf.readBoolean();
        SupportedRecipeExporter exporter = buf.readBoolean() ? buf.readEnum(SupportedRecipeExporter.class) : null;
        String name = buf.readUtf();
        String path = buf.readUtf();
        String json = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        return new RecipeFeedbackPacket(RecipeRequestFeedback.of(feedback, success, exporter, name.isEmpty() ? null : name, json.isEmpty() ? null : json, path.isEmpty() ? null : path));
    }

    public static void handleClient(RecipeFeedbackPacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> NeoForge1211Platform.get().getNetworkInteractionAdapter().handleClientRecipeFeedback(packet.feedback()));
    }
}
