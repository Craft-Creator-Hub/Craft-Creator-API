package fr.en0ri4n.craftcreator.api.blockentity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
public class CoreBlockEntity
{

    private final UUID uuid;
    private final Identifier typeId; // the definition id
    private final List<CoreItemStack> inventory;
    private final BlockEntityBehavior behavior;
    private final JsonObject extraData; // JSON tree for behaviors

    public CoreBlockEntity(Identifier typeId, BlockEntityBehavior behavior, int inventorySize)
    {
        this.uuid = UUID.randomUUID();
        this.typeId = typeId;
        this.inventory = new ArrayList<>(inventorySize);
        // initialize inventory with empty slots
        for(int i = 0; i < inventorySize; i++) inventory.add(CoreItemStack.EMPTY);
        this.behavior = behavior;
        this.extraData = new JsonObject();
    }

    /* ---- helpers to manipulate inventory ---- */

    public void setSlot(int slot, CoreItemStack stack)
    {
        if(slot < 0 || slot >= inventory.size()) return;
        inventory.set(slot, stack == null ? CoreItemStack.EMPTY : stack);
    }

    public CoreItemStack getSlot(int slot)
    {
        if(slot < 0 || slot >= inventory.size()) return null;
        return inventory.get(slot);
    }

    public int getInventorySize()
    {
        return inventory.size();
    }

    public boolean isInventoryEmpty()
    {
        for(CoreItemStack s : inventory)
        {
            if(s != null && s.getCount() > 0) return false;
        }
        return true;
    }

    /* ---- serialization ---- */

    public JsonObject toJson()
    {
        JsonObject root = new JsonObject();
        root.addProperty("uuid", uuid.toString());
        root.addProperty("type", typeId.toString());

        JsonArray inv = new JsonArray();
        for(int i = 0; i < inventory.size(); i++)
        {
            CoreItemStack s = inventory.get(i);
            s.setSlotIndex(i);
            if(s != CoreItemStack.EMPTY) inv.add(s.toJson());
        }
        root.add("inventory", inv);
        root.add("behaviorData", fetchBehaviorData());
        root.add("extra", extraData.deepCopy());

        return root;
    }

    public static CoreBlockEntity fromJson(JsonObject json, CoreBlockEntityDefinition def)
    {
        Identifier type = Identifier.from(json.get("type").getAsString());
        BlockEntityBehavior behavior = CoreBlockEntityManager.get().getBehavior(type).get();
        CoreBlockEntity entity = new CoreBlockEntity(type, behavior, def.getInventorySize());

        if(json.has("uuid"))
        {
            /* ignore - uuid is final; can't set; keep generated */
        }

        if(json.has("inventory"))
        {
            JsonArray arr = json.getAsJsonArray("inventory");
            Collections.fill(entity.inventory, CoreItemStack.EMPTY);
            for(int i = 0; i < arr.size(); i++)
            {
                JsonObject o = arr.get(i).getAsJsonObject();
                CoreItemStack stack = CoreItemStack.fromJson(o);
                entity.inventory.set(stack.getSlotIndex(), stack);
            }
        }

        if(json.has("behaviorData"))
        {
            JsonObject behData = json.getAsJsonObject("behaviorData");
            entity.updateBehaviorData(behData);
        }

        if(json.has("extra"))
        {
            entity.extraData.add("extra", json.get("extra").getAsJsonObject());
        }

        return entity;
    }

    public void updateBehaviorData(JsonObject payload)
    {
        behavior.load(this, payload);
    }

    public JsonObject fetchBehaviorData()
    {
        JsonObject out = new JsonObject();
        behavior.save(this, out);
        return out;
    }

    public CoreItemStack removeItem(int slot, int amount)
    {
        CoreItemStack current = getSlot(slot);
        if(current == null || current.getCount() <= 0 || amount <= 0)
        {
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
        if(current == null || current.getCount() <= 0)
        {
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