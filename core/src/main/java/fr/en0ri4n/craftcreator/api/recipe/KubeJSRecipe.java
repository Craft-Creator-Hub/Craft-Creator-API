package fr.en0ri4n.craftcreator.api.recipe;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.recipe.utils.KubeJSRecipeAction;
import fr.en0ri4n.craftcreator.utils.FormattableString;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.RecipeDescriptors;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class KubeJSRecipe extends AbstractCCRecipe
{
    public static final FormattableString BASE_LINE = FormattableString.of("event.%s(%s)");

    private final KubeJSRecipeAction type;
    private final Map<RecipeDescriptors, String> recipeDescriptors;

    public KubeJSRecipe(KubeJSRecipeAction type)
    {
        this.type = type;
        this.recipeDescriptors = new HashMap<>();
    }

    public String getInputItem()
    {
        return recipeDescriptors.get(RecipeDescriptors.INPUT_ITEM);
    }

    public String getOutputItem()
    {
        return recipeDescriptors.get(RecipeDescriptors.OUTPUT_ITEM);
    }

    public String getModId()
    {
        return recipeDescriptors.get(RecipeDescriptors.MOD_ID);
    }

    public Identifier getRecipeType()
    {
        return Identifier.from(recipeDescriptors.get(RecipeDescriptors.RECIPE_TYPE));
    }

    @Override
    public String toString()
    {
        return "";
    }

    public String getRecipeId()
    {
        return recipeDescriptors.get(RecipeDescriptors.RECIPE_ID);
    }

    public void setDescriptor(RecipeDescriptors descriptor, String value)
    {
        recipeDescriptors.put(descriptor, value);
    }

    public Map<RecipeDescriptors, String> getRecipeMap()
    {
        return this.recipeDescriptors;
    }

//    @Override
//    public FormattableString getBaseLine()
//    {
//        return BASE_LINE;
//    }

    @Override
    public void deserialize(JsonObject jsonObject)
    {

    }

    @Override
    public JsonObject serialize()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", type.getDescriptor());
        for(Map.Entry<RecipeDescriptors, String> entry : recipeDescriptors.entrySet())
        {
            jsonObject.addProperty(entry.getKey().getTag(), entry.getValue());
        }
        return jsonObject;
    }

    public static KubeJSRecipe deserialize(String jsonStr)
    {
        JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);

        KubeJSRecipeAction type = KubeJSRecipeAction.byDescriptor(jsonObject.get("modified_type").getAsString());
        if(type != null)
        {
            KubeJSRecipe recipe = new KubeJSRecipe(type);
            for(RecipeDescriptors descriptor : RecipeDescriptors.values())
                if(jsonObject.has(descriptor.getTag()))
                    recipe.setDescriptor(descriptor, jsonObject.get(descriptor.getTag()).getAsString());

            return recipe;
        }

        return null;
    }
}
