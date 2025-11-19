package fr.en0ri4n.craftcreator.impl.model;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.impl.model.container.minecraft.CraftingTableRecipeCreatorContainerModel;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContainerModels
{
    private static final ContainerModels INSTANCE = new ContainerModels();

    private final Map<String, ContainerModel<?>> containerModels = new HashMap<>();

    public static ContainerModels get()
    {
        return INSTANCE;
    }

    public ContainerModel<?> getContainerModel(Identifier id, CoreBlockPos pos)
    {
        if(!containerModels.containsKey(id.toString())) throw new IllegalArgumentException("Unknown container model: " + id);

        ContainerModel<?> model = containerModels.get(id.toString());
        model.setBlockEntityPos(pos);
        return containerModels.get(id.toString());
    }

    public void registerAll()
    {
        containerModels.put(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR.toString(), new CraftingTableRecipeCreatorContainerModel());
    }
}
