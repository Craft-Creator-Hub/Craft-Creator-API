package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.recipe.slot.SlotRecipeType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Behavior of recipe creators built from a {@link SlotRecipeType}: stores the values typed in the settings panel.
 */
@Getter
public class SlotRecipeBehavior extends RecipeCreatorBlockEntityBehavior
{
    private static final String PARAMS_KEY = "params";

    private final SlotRecipeType type;
    /** Values typed by the player, by param key. */
    private final Map<String, String> params = new HashMap<>();

    public SlotRecipeBehavior(SlotRecipeType type)
    {
        this.type = type;
    }

    public void setParam(String key, String value)
    {
        params.put(key, value);
    }

    @Override
    public void save(CoreBlockEntity entity, JsonObject out)
    {
        super.save(entity, out);
        JsonObject paramsObj = new JsonObject();
        params.forEach(paramsObj::addProperty);
        out.add(PARAMS_KEY, paramsObj);
    }

    @Override
    public void load(CoreBlockEntity entity, JsonObject in)
    {
        super.load(entity, in);
        if(in.has(PARAMS_KEY))
        {
            params.clear();
            in.getAsJsonObject(PARAMS_KEY).entrySet().forEach(e -> params.put(e.getKey(), e.getValue().getAsString()));
        }
    }
}
