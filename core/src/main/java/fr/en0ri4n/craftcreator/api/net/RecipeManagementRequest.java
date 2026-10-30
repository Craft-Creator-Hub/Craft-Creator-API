package fr.en0ri4n.craftcreator.api.net;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.serialize.GsonProvider;
import lombok.Getter;

/**
 * Client to server request from the recipe management screens. The server answers with a {@link RecipeManagementState}.
 */
@Getter
public class RecipeManagementRequest
{
    private final Action action;
    private final SupportedRecipeExporter exporter;
    /** Action argument: {"id": ...} for DELETE_ADDED, the removal filter for REMOVE and RESTORE, empty for LIST. */
    private final JsonObject payload;

    public RecipeManagementRequest(Action action, SupportedRecipeExporter exporter, JsonObject payload)
    {
        this.action = action;
        this.exporter = exporter;
        this.payload = payload == null ? new JsonObject() : payload;
    }

    public static RecipeManagementRequest list(SupportedRecipeExporter exporter)
    {
        return new RecipeManagementRequest(Action.LIST, exporter, null);
    }

    public String toJsonString()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", action.name());
        obj.addProperty("exporter", exporter.name());
        obj.add("payload", payload);
        return GsonProvider.compactGson().toJson(obj);
    }

    public static RecipeManagementRequest fromJsonString(String json)
    {
        JsonObject obj = GsonProvider.compactGson().fromJson(json, JsonObject.class);
        return new RecipeManagementRequest(Action.valueOf(obj.get("action").getAsString()),
                                           SupportedRecipeExporter.valueOf(obj.get("exporter").getAsString()),
                                           obj.getAsJsonObject("payload"));
    }

    public enum Action
    {
        /** Only fetch the state. */
        LIST,
        /** Delete a recipe added through the exporter. */
        DELETE_ADDED,
        /** Remove existing recipes matching a filter (KubeJS only). */
        REMOVE,
        /** Cancel a removal (KubeJS only). */
        RESTORE
    }
}
