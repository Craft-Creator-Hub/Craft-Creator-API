package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityBehavior;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TaggableSlotsBlockEntityBehavior implements BlockEntityBehavior
{
    private final Map<Integer, Identifier> taggedSlots;

    public TaggableSlotsBlockEntityBehavior()
    {
        this.taggedSlots = new HashMap<>();
    }

    @Override
    public void save(CoreBlockEntity entity, JsonObject out)
    {
        JsonArray tagsArray = new JsonArray();
        for (Map.Entry<Integer, Identifier> entry : taggedSlots.entrySet())
        {
            JsonObject tagEntry = new JsonObject();
            tagEntry.addProperty("slot", entry.getKey());
            tagEntry.addProperty("tag", entry.getValue().toString());
            tagsArray.add(tagEntry);
        }
        out.add("taggedSlots", tagsArray);
    }

    @Override
    public void load(CoreBlockEntity entity, JsonObject in)
    {
        taggedSlots.clear();
        if (in.has("taggedSlots"))
        {
            JsonArray tagsArray = in.getAsJsonArray("taggedSlots");
            for (int i = 0; i < tagsArray.size(); i++)
            {
                JsonObject tagEntry = tagsArray.get(i).getAsJsonObject();
                int slot = tagEntry.get("slot").getAsInt();
                Identifier tag = Identifier.from(tagEntry.get("tag").getAsString());
                taggedSlots.put(slot, tag);
            }
        }
    }
}
