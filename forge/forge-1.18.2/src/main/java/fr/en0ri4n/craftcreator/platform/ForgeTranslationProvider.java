package fr.en0ri4n.craftcreator.platform;

import fr.en0ri4n.craftcreator.api.translations.TranslationProvider;
import net.minecraft.locale.Language;

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
        return Language.getInstance().getOrDefault(key).formatted(args);
    }
}
