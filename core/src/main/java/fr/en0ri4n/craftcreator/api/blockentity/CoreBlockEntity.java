package fr.en0ri4n.craftcreator.api.blockentity;

import com.google.gson.*;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Core container for block-entity data. Platform-independent and serializable to JSON.
 * Behaviors are resolved via CoreBlockEntityManager using registered behavior ids.
 */
@Getter
public class CoreBlockEntity {

    private final UUID uuid;
    private final Identifier typeId; // the definition id
    private final List<CoreItemStack> inventory;
    private final JsonObject extraData; // JSON tree for behaviors

    public CoreBlockEntity(Identifier typeId, int inventorySize) {
        this.uuid = UUID.randomUUID();
        this.typeId = typeId;
        this.inventory = new ArrayList<>(inventorySize);
        // initialize inventory with empty slots
        for (int i = 0; i < inventorySize; i++) inventory.add(CoreItemStack.EMPTY);
        this.extraData = new JsonObject();
    }

    /* ---- helpers to manipulate inventory ---- */

    public void setSlot(int slot, CoreItemStack stack) {
        if (slot < 0 || slot >= inventory.size()) return;
        inventory.set(slot, stack == null ? CoreItemStack.EMPTY : stack);
    }

    public CoreItemStack getSlot(int slot) {
        if (slot < 0 || slot >= inventory.size()) return null;
        return inventory.get(slot);
    }

    public int getInventorySize() { return inventory.size(); }

    public boolean isInventoryEmpty() {
        for (CoreItemStack s : inventory) {
            if (s != null && s.getCount() > 0) return false;
        }
        return true;
    }

    /* ---- serialization ---- */

    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.addProperty("uuid", uuid.toString());
        root.addProperty("type", typeId.toString());

        JsonArray inv = new JsonArray();
        for (CoreItemStack s : inventory) inv.add(s.toJson());
        root.add("inventory", inv);
        root.add("extra", extraData.deepCopy());

        return root;
    }

    public static CoreBlockEntity fromJson(JsonObject json, CoreBlockEntityDefinition def) {
        Identifier type = Identifier.from(json.get("type").getAsString());
        CoreBlockEntity entity = new CoreBlockEntity(type, def.getInventorySize());

        if (json.has("uuid")) {
            /* ignore - uuid is final; can't set; keep generated */
        }

        if (json.has("inventory")) {
            JsonArray arr = json.getAsJsonArray("inventory");
            for (int i = 0; i < Math.min(arr.size(), entity.inventory.size()); i++) {
                JsonObject o = arr.get(i).getAsJsonObject();
                entity.inventory.set(i, CoreItemStack.fromJson(o));
            }
        }

        if (json.has("extra")) {
            entity.extraData.add("extra", json.get("extra").getAsJsonObject());
        }

        return entity;
    }

    public void updateData(JsonObject payload) {
        for (String key : payload.keySet()) {
            JsonElement val = payload.get(key);
            extraData.add(key, val.deepCopy());
        }
    }

    public JsonObject fetchData() {
        return extraData.deepCopy();
    }

    public CoreItemStack removeItem(int slot, int amount)
    {
        CoreItemStack current = getSlot(slot);
        if (current == null || current.getCount() <= 0 || amount <= 0) {
            return CoreItemStack.EMPTY;
        }

        int toRemove = Math.min(amount, current.getCount());
        Identifier itemId = current.getItemId();

        // Update the core stack
        int remaining = current.getCount() - toRemove;
        setSlot(slot, new CoreItemStack(itemId, remaining));

        return new CoreItemStack(itemId, toRemove);
    }

    public CoreItemStack removeItemNoUpdate(int slot)
    {
        CoreItemStack current = getSlot(slot);
        if (current == null || current.getCount() <= 0) {
            return CoreItemStack.EMPTY;
        }

        setSlot(slot, CoreItemStack.EMPTY);
        return current;
    }

    public void clearInventory()
    {
        Collections.fill(inventory, CoreItemStack.EMPTY);
    }
}