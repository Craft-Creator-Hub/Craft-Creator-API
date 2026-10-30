package fr.en0ri4n.craftcreator.recipe.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;

/**
 * Readable names and values for recipe parameters, e.g. "cookingtime" = 200 becomes "Cooking time" = "200 ticks (10s)".
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecipeParameterFormat
{
    private static final int MAX_DECIMALS = 4;
    private static final double TICKS_PER_SECOND = 20D;

    private static final Map<String, String> NAMES = Map.ofEntries(
            Map.entry("cookingtime", "Cooking time"),
            Map.entry("processingtime", "Processing time"),
            Map.entry("time", "Duration"),
            Map.entry("experience", "Experience"),
            Map.entry("energy", "Energy"),
            Map.entry("mana", "Mana"),
            Map.entry("watermod", "Water modifier"),
            Map.entry("count", "Result count"));

    private enum Unit { NONE, TICKS, EXPERIENCE, ENERGY, PERCENT, MULTIPLIER }

    /**
     * @param key a json key or parameter name, in any case style ("processingTime", "processing_time"...)
     */
    public static String name(String key)
    {
        String normalized = normalize(key);
        if(NAMES.containsKey(normalized)) return NAMES.get(normalized);

        if(normalized.startsWith("chance") && normalized.length() > "chance".length())
        {
            String index = normalized.substring("chance".length());
            if(index.chars().allMatch(Character::isDigit)) return "Chance " + (Integer.parseInt(index) + 1);
        }

        String spaced = key.replaceAll("([a-z])([A-Z])", "$1 $2").replace('_', ' ').trim().toLowerCase(Locale.ROOT);
        return spaced.isEmpty() ? spaced : Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    /**
     * @param raw the raw value; non-numbers are returned as they are
     */
    public static String value(String key, String raw)
    {
        BigDecimal number;
        try
        {
            number = new BigDecimal(raw.trim());
        }
        catch(NumberFormatException | NullPointerException e)
        {
            return raw == null ? "" : raw;
        }

        return switch(unitOf(normalize(key)))
        {
            case TICKS -> format(number) + " ticks (" + format(number.divide(BigDecimal.valueOf(TICKS_PER_SECOND), MAX_DECIMALS, RoundingMode.HALF_UP)) + "s)";
            case EXPERIENCE -> format(number) + " XP";
            case ENERGY -> format(number) + " RF";
            case PERCENT -> format(number.multiply(BigDecimal.valueOf(100))) + "%";
            case MULTIPLIER -> "x" + format(number);
            case NONE -> format(number);
        };
    }

    private static Unit unitOf(String normalizedKey)
    {
        if(normalizedKey.endsWith("time")) return Unit.TICKS;
        if(normalizedKey.equals("experience")) return Unit.EXPERIENCE;
        if(normalizedKey.equals("energy")) return Unit.ENERGY;
        if(normalizedKey.startsWith("chance")) return Unit.PERCENT;
        if(normalizedKey.endsWith("mod")) return Unit.MULTIPLIER;
        return Unit.NONE;
    }

    /**
     * At most 4 decimals without trailing zeros, thousands grouped for whole numbers: 0.7, 0.3333, 500,000.
     */
    public static String format(BigDecimal number)
    {
        BigDecimal rounded = number.setScale(MAX_DECIMALS, RoundingMode.HALF_UP).stripTrailingZeros();
        if(rounded.scale() <= 0) return String.format(Locale.ROOT, "%,d", rounded.toBigInteger());
        return rounded.toPlainString();
    }

    private static String normalize(String key)
    {
        return key.replace("_", "").toLowerCase(Locale.ROOT);
    }
}
