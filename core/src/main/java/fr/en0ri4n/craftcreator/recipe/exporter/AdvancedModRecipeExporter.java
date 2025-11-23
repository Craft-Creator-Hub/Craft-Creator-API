package fr.en0ri4n.craftcreator.recipe.exporter;

import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

public abstract class AdvancedModRecipeExporter extends ModRecipeExporter
{
    public abstract void removeRecipe(Identifier recipeId);

    public abstract void removeAllOfType(Identifier recipeTypeId);

    public abstract List<Recipe> getRemovedRecipes();
}
