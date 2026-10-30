package fr.en0ri4n.craftcreator.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Client-side approximation of KubeJS recipe filters, to show which recipes a removal or a replacement affects before /reload.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecipeFilters
{
    /**
     * Same keys as KubeJS filters ("id", "type", "mod", "input", "output"): every key present must match.
     */
    public static boolean matches(JsonObject filter, Recipe recipe)
    {
        for(Map.Entry<String, JsonElement> e : filter.entrySet())
        {
            String value = e.getValue().getAsString();
            boolean match = switch(e.getKey())
            {
                case "id" -> recipe.getId().toString().equals(value);
                case "type" -> recipe.getType().toString().equals(value);
                case "mod" -> recipe.getId().getNamespace().equals(value);
                case "output" -> recipe.getOutputs().stream().anyMatch(output -> output.getId().toString().equals(value));
                case "input" -> recipe.getInputs().stream().anyMatch(input -> accepts(input, value));
                default -> false;
            };
            if(!match) return false;
        }
        return true;
    }

    /**
     * Whether a replacement ({"kind", "filter", "from", "to"}) changes this recipe.
     */
    public static boolean affects(JsonObject replacement, Recipe recipe)
    {
        if(!matches(replacement.getAsJsonObject("filter"), recipe)) return false;

        String from = replacement.get("from").getAsString();
        return replacement.get("kind").getAsString().equals("input")
                ? recipe.getInputs().stream().anyMatch(input -> accepts(input, from))
                : recipe.getOutputs().stream().anyMatch(output -> output.getId().toString().equals(from));
    }

    /**
     * @param value an item id or a #tag
     */
    public static boolean accepts(RecipeEntry input, String value)
    {
        if(input.isTag()) return value.equals("#" + input.getId());
        return input.getId().toString().equals(value) || input.getAlternatives().stream().anyMatch(id -> id.toString().equals(value));
    }

    /**
     * Short text for a replacement, e.g. "input: minecraft:stick -> #minecraft:saplings".
     */
    public static String describe(JsonObject replacement)
    {
        return replacement.get("kind").getAsString() + ": " + replacement.get("from").getAsString() + " -> " + replacement.get("to").getAsString();
    }
}
