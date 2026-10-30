package fr.en0ri4n.craftcreator.api.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.serialize.GsonProvider;
import fr.en0ri4n.craftcreator.utils.Feedback;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Server answer to a {@link RecipeManagementRequest}: the exporter's current recipes and removals, plus the result of the action.
 */
@Getter
public class RecipeManagementState
{
    private final SupportedRecipeExporter exporter;
    private final List<JsonObject> addedRecipes;
    private final List<JsonObject> removals;
    private final List<JsonObject> replacements;
    /** Whether the exporter can remove existing recipes. */
    private final boolean removalSupported;
    /** Result of the action, null for LIST. */
    private final Feedback feedback;
    private final boolean success;

    public RecipeManagementState(SupportedRecipeExporter exporter, List<JsonObject> addedRecipes, List<JsonObject> removals, List<JsonObject> replacements, boolean removalSupported, Feedback feedback, boolean success)
    {
        this.exporter = exporter;
        this.addedRecipes = List.copyOf(addedRecipes);
        this.removals = List.copyOf(removals);
        this.replacements = List.copyOf(replacements);
        this.removalSupported = removalSupported;
        this.feedback = feedback;
        this.success = success;
    }

    public String toJsonString()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("exporter", exporter.name());
        obj.add("added", toArray(addedRecipes));
        obj.add("removals", toArray(removals));
        obj.add("replacements", toArray(replacements));
        obj.addProperty("removalSupported", removalSupported);
        if(feedback != null) obj.addProperty("feedback", feedback.name());
        obj.addProperty("success", success);
        return GsonProvider.compactGson().toJson(obj);
    }

    public static RecipeManagementState fromJsonString(String json)
    {
        JsonObject obj = GsonProvider.compactGson().fromJson(json, JsonObject.class);
        return new RecipeManagementState(SupportedRecipeExporter.valueOf(obj.get("exporter").getAsString()),
                                         fromArray(obj.getAsJsonArray("added")),
                                         fromArray(obj.getAsJsonArray("removals")),
                                         fromArray(obj.getAsJsonArray("replacements")),
                                         obj.get("removalSupported").getAsBoolean(),
                                         obj.has("feedback") ? Feedback.valueOf(obj.get("feedback").getAsString()) : null,
                                         obj.get("success").getAsBoolean());
    }

    private static JsonArray toArray(List<JsonObject> objects)
    {
        JsonArray array = new JsonArray();
        objects.forEach(array::add);
        return array;
    }

    private static List<JsonObject> fromArray(JsonArray array)
    {
        List<JsonObject> objects = new ArrayList<>();
        for(JsonElement element : array)
            objects.add(element.getAsJsonObject());
        return objects;
    }
}
