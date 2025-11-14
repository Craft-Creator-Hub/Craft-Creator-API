package fr.en0ri4n.craftcreator.api.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.JsonSerializable;

public abstract class AbstractCCRecipe implements JsonSerializable
{
    protected static final Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).create();

    public abstract Identifier getRecipeType();

    @Override
    public abstract String toString();
}