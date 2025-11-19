package fr.en0ri4n.craftcreator.api.item;

import fr.en0ri4n.craftcreator.utils.Identifier;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Minimal, loader-agnostic serializable item representation for core inventories.
 * Platforms map Identifier -> actual ItemStack when needed.
 */
@Getter
public class CoreItemStack {
    public static final CoreItemStack EMPTY = new CoreItemStack(Identifier.from("minecraft:air"), 0);

    private final Identifier itemId;
    private final int count;
    private final JsonObject nbt; // optional extra data as JSON
    @Setter
    private int slotIndex = -1; // optional slot index in an inventory

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
        if(slotIndex >= 0) obj.addProperty("slotIndex", slotIndex);
        return obj;
    }

    public static CoreItemStack fromJson(JsonObject obj) {
        Identifier id = Identifier.from(obj.get("id").getAsString());
        int count = obj.has("count") ? obj.get("count").getAsInt() : 0;
        JsonObject nbt = obj.has("nbt") ? obj.get("nbt").getAsJsonObject() : null;
        CoreItemStack cis = new CoreItemStack(id, count, nbt);
        if(obj.has("slotIndex")) cis.setSlotIndex(obj.get("slotIndex").getAsInt());
        return cis;
    }
}