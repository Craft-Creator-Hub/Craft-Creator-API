package fr.en0ri4n.craftcreator.impl.blockentity.behaviors;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import lombok.Getter;
import lombok.Setter;

/**
 * Base behavior for recipe creator block entities.
 * <p>
 * Used only as a marker class for now, so we can update this class without affecting other behaviors.
 */
@Getter
@Setter
public abstract class RecipeCreatorBlockEntityBehavior extends TaggableSlotsBlockEntityBehavior
{
    private SupportedRecipeExporter serializationType = SupportedRecipeExporter.MINECRAFT_DATAPACK;

    @Override
    public void load(CoreBlockEntity entity, JsonObject in)
    {
        super.load(entity, in);
        if(in.has("serializationType"))
            this.serializationType = SupportedRecipeExporter.valueOf(in.get("serializationType").getAsString().toUpperCase());
    }

    @Override
    public void save(CoreBlockEntity entity, JsonObject out)
    {
        super.save(entity, out);
        if(this.serializationType != null)
            out.addProperty("serializationType", this.serializationType.name().toLowerCase());
    }
}
