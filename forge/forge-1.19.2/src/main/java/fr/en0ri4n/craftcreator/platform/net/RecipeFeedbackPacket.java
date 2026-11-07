package fr.en0ri4n.craftcreator.platform.net;

import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.platform.Forge1192Platform;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import fr.en0ri4n.craftcreator.utils.Feedback;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * Server to client: the answer to a recipe request (success or error message, and the recipe's json for a preview).
 * The json is written as a byte array, as it can be longer than a size-limited string.
 */
public record RecipeFeedbackPacket(RecipeRequestFeedback feedback)
{
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

    public static void handleClient(RecipeFeedbackPacket packet, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> Forge1192Platform.get().getNetworkInteractionAdapter().handleClientRecipeFeedback(packet.feedback()));
        ctx.setPacketHandled(true);
    }
}
