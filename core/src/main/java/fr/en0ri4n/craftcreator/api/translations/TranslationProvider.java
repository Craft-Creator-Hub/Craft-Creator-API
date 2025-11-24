package fr.en0ri4n.craftcreator.api.translations;

public interface TranslationProvider
{
    String translate(String key, Object... args);
}
