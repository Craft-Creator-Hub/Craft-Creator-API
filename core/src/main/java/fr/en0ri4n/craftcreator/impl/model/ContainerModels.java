package fr.en0ri4n.craftcreator.impl.model;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.impl.model.container.minecraft.CraftingTableRecipeCreatorContainerModel;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ContainerModels
{
    public static final ContainerModels INSTANCE = new ContainerModels();

    private final Map<String, ContainerModel> containerModels = new HashMap<>();

    private ContainerModels() {}

    public static ContainerModels get() { return INSTANCE; }

    public ContainerModel getContainerModel(Identifier id) {
        return containerModels.get(id.toString());
    }

    public void registerAll() {
        containerModels.put(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR.toString(), new CraftingTableRecipeCreatorContainerModel());
    }
}
