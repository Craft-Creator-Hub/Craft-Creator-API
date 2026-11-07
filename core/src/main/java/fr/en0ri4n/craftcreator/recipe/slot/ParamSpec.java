package fr.en0ri4n.craftcreator.recipe.slot;

import java.util.List;

/**
 * A value set by the player in the recipe settings panel (mana, energy, cooking time...).
 *
 * @param key          key used by the json writer and in the saved block entity data
 * @param label        translation key or plain text shown next to the input
 * @param type         accepted values
 * @param defaultValue value used until the player changes it
 * @param options      values a {@link Type#CHOICE} can take, empty for other types
 */
public record ParamSpec(String key, String label, Type type, String defaultValue, List<String> options)
{
    public enum Type
    {
        INTEGER,
        FLOAT,
        STRING,
        /** One of {@link #options()}, picked with a button cycling through them. */
        CHOICE,
        /** "true" or "false", picked with a Yes/No button. */
        BOOLEAN
    }

    public ParamSpec
    {
        options = List.copyOf(options);
    }

    public static ParamSpec integer(String key, String label, int defaultValue)
    {
        return new ParamSpec(key, label, Type.INTEGER, String.valueOf(defaultValue), List.of());
    }

    public static ParamSpec decimal(String key, String label, double defaultValue)
    {
        return new ParamSpec(key, label, Type.FLOAT, String.valueOf(defaultValue), List.of());
    }

    public static ParamSpec text(String key, String label, String defaultValue)
    {
        return new ParamSpec(key, label, Type.STRING, defaultValue, List.of());
    }

    /**
     * @param options the values it can take; the first one is the default
     */
    public static ParamSpec choice(String key, String label, String... options)
    {
        return new ParamSpec(key, label, Type.CHOICE, options[0], List.of(options));
    }

    public static ParamSpec bool(String key, String label, boolean defaultValue)
    {
        return new ParamSpec(key, label, Type.BOOLEAN, String.valueOf(defaultValue), List.of("true", "false"));
    }
}
