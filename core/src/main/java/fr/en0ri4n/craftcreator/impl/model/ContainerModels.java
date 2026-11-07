package fr.en0ri4n.craftcreator.impl.model;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreator;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContainerModels
{
    private static final ContainerModels INSTANCE = new ContainerModels();
    public static ContainerModels get()
    {
        return INSTANCE;
    }

    private final Map<String, ContainerModel<?>> containerModels = new HashMap<>();

    public ContainerModel<?> getContainerModel(Identifier id, CoreBlockPos pos)
    {
        if(!containerModels.containsKey(id.toString())) throw new IllegalArgumentException("Unknown container model: " + id);

        ContainerModel<?> model = containerModels.get(id.toString());
        model.setBlockEntityPos(pos);
        return containerModels.get(id.toString());
    }

    public void registerAll()
    {
        for(RecipeCreator<?> creator : RecipeCreators.ALL_RECIPE_CREATORS)
            containerModels.put(creator.getId().toString(), creator.getContainerModel());
    }
}
