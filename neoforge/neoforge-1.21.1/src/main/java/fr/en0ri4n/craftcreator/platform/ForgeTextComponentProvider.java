package fr.en0ri4n.craftcreator.platform;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.translations.TextComponentProvider;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class ForgeTextComponentProvider implements TextComponentProvider<Component>
{
    private static final ForgeTextComponentProvider INSTANCE = new ForgeTextComponentProvider();

    public static TextComponentProvider<Component> get()
    {
        return INSTANCE;
    }

    @Override
    public String translateToString(String key, Object... args)
    {
        return translate(key, args).getString();
    }

    @Override
    public Component translate(String key, Object... args)
    {
        return Component.translatable(key, args);
    }

    @Override
    public Component literal(String text)
    {
        return Component.literal(text);
    }

    @Override
    public Component createFeedbackComponent(RecipeRequestFeedback feedback)
    {
        if(!feedback.isSuccess())
        {
            // For simplicity, we use the feedback's message key directly.
            return literal(CraftCreatorAPI.translateToString(feedback.getFeedback().getMessageKey()));
        }

        // TODO: Translation keys for the labels and hover texts
        MutableComponent component = Component.literal("{ < = - [Craft-Creator] - = > } }").append("\n").withStyle(ChatFormatting.GRAY);

        MutableComponent exporterInfo = Component.literal("Exporter: ").withStyle(ChatFormatting.WHITE)
            .append(Component.literal(feedback.getRecipeType() != null ? feedback.getRecipeType().toString() : "Unknown").withStyle(ChatFormatting.AQUA))
            .append("\n");
        component.append(exporterInfo);

        MutableComponent nameInfo = Component.literal("Name: ").withStyle(ChatFormatting.WHITE)
            .append(Component.literal(feedback.getRecipeName() != null ? feedback.getRecipeName() : "N/A").withStyle(ChatFormatting.LIGHT_PURPLE))
            .append("\n");
        component.append(nameInfo);

        MutableComponent openRecipe = Component.literal("[Open Recipe] ").withStyle(style -> style
            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, feedback.getRecipePath() != null ? feedback.getRecipePath() : ""))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(feedback.getRecipePath() != null ? feedback.getRecipePath() : "No file path available").withStyle(ChatFormatting.GRAY))))
            .withStyle(ChatFormatting.GREEN);
        component.append(openRecipe);

        MutableComponent copyJson = Component.literal("[Copy JSON] ").withStyle(style -> style
            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, feedback.getRecipeJson() != null ? feedback.getRecipeJson() : ""))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(feedback.getRecipeJson() != null ? feedback.getRecipeJson() : "No JSON data available").withStyle(ChatFormatting.GRAY))))
            .withStyle(ChatFormatting.YELLOW);
        component.append(copyJson);

        return component;
    }
}

