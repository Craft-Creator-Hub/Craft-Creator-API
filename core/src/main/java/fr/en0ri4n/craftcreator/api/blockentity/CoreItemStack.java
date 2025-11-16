package fr.en0ri4n.craftcreator.api.blockentity;

import fr.en0ri4n.craftcreator.utils.Identifier;
import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Minimal, loader-agnostic serializable item representation for core inventories.
 * Platforms map Identifier -> actual ItemStack when needed.
 */
@Getter
public final class CoreItemStack {
    private final Identifier itemId;
    private final int count;
    private final JsonObject nbt; // optional extra data as JSON

    public CoreItemStack(Identifier itemId, int count) {
        this(itemId, count, null);
    }

    public CoreItemStack(Identifier itemId, int count, JsonObject nbt) {
        this.itemId = itemId;
        this.count = Math.max(0, count);
        this.nbt = nbt;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", itemId.toString());
        obj.addProperty("count", count);
        if (nbt != null) obj.add("nbt", nbt);
        return obj;
    }

    public static CoreItemStack fromJson(JsonObject obj) {
        Identifier id = Identifier.from(obj.get("id").getAsString());
        int count = obj.has("count") ? obj.get("count").getAsInt() : 0;
        JsonObject nbt = obj.has("nbt") ? obj.get("nbt").getAsJsonObject() : null;
        return new CoreItemStack(id, count, nbt);
    }
}