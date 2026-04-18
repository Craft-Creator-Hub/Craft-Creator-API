package fr.en0ri4n.craftcreator.platform;

import fr.en0ri4n.craftcreator.api.translations.TranslationProvider;
import net.minecraft.network.chat.TranslatableComponent;

public class ForgeTranslationProvider implements TranslationProvider
{
    private static final ForgeTranslationProvider INSTANCE = new ForgeTranslationProvider();

    public static TranslationProvider get()
    {
        return INSTANCE;
    }

    @Override
    public String translate(String key, Object... args)
    {
        return new TranslatableComponent(key, args).getString();
    }
}
