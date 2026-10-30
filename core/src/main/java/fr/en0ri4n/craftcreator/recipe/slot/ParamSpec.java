package fr.en0ri4n.craftcreator.recipe.slot;

/**
 * A value typed by the player in the recipe settings panel (mana, energy, cooking time...).
 *
 * @param key          key used by the json writer and in the saved block entity data
 * @param label        translation key or plain text shown next to the input
 * @param type         accepted values
 * @param defaultValue value used until the player changes it
 */
public record ParamSpec(String key, String label, Type type, String defaultValue)
{
    public enum Type
    {
        INTEGER,
        FLOAT,
        STRING
    }

    public static ParamSpec integer(String key, String label, int defaultValue)
    {
        return new ParamSpec(key, label, Type.INTEGER, String.valueOf(defaultValue));
    }

    public static ParamSpec decimal(String key, String label, double defaultValue)
    {
        return new ParamSpec(key, label, Type.FLOAT, String.valueOf(defaultValue));
    }

    public static ParamSpec text(String key, String label, String defaultValue)
    {
        return new ParamSpec(key, label, Type.STRING, defaultValue);
    }
}
