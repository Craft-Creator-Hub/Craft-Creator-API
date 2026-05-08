package fr.en0ri4n.craftcreator.platform;

import fr.en0ri4n.craftcreator.api.translations.TextComponentProvider;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

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
        return new TranslatableComponent(key, args).getString();
    }

    @Override
    public Component translate(String key, Object... args)
    {
        return new TranslatableComponent(key, args);
    }

    @Override
    public Component literal(String text)
    {
        return new TextComponent(text);
    }

    @Override
    public Component createFeedbackComponent(RecipeRequestFeedback feedback)
    {
        // TODO: Translation keys for the labels and hover texts
        MutableComponent component = new TextComponent("{ < = - [Craft-Creator] - = > } }").append("\n").withStyle(ChatFormatting.GRAY);

        MutableComponent exporterInfo = new TextComponent("Exporter: ").withStyle(ChatFormatting.WHITE)
            .append(new TextComponent(feedback.getRecipeType() != null ? feedback.getRecipeType().toString() : "Unknown").withStyle(ChatFormatting.AQUA))
            .append("\n");
        component.append(exporterInfo);

        MutableComponent nameInfo = new TextComponent("Name: ").withStyle(ChatFormatting.WHITE)
            .append(new TextComponent(feedback.getRecipeName() != null ? feedback.getRecipeName() : "N/A").withStyle(ChatFormatting.LIGHT_PURPLE))
            .append("\n");
        component.append(nameInfo);

        MutableComponent openRecipe = new TextComponent("[Open Recipe] ").withStyle(style -> style
            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, feedback.getRecipePath() != null ? feedback.getRecipePath() : ""))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(feedback.getRecipePath() != null ? feedback.getRecipePath() : "No file path available").withStyle(ChatFormatting.GRAY))))
            .withStyle(ChatFormatting.GREEN);
        component.append(openRecipe);

        MutableComponent copyJson = new TextComponent("[Copy JSON] ").withStyle(style -> style
            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, feedback.getRecipeJson() != null ? feedback.getRecipeJson() : ""))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(feedback.getRecipeJson() != null ? feedback.getRecipeJson() : "No JSON data available").withStyle(ChatFormatting.GRAY))))
            .withStyle(ChatFormatting.YELLOW);
        component.append(copyJson);

        return component;
    }
}

