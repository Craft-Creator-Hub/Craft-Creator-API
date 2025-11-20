package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;

public abstract class RecipeCreatorContainerScreenDefinition<T extends RecipeCreatorBlockEntityBehavior> extends TaggableSlotsContainerScreenDefinition<T>
{
    public RecipeCreatorContainerScreenDefinition(ContainerModel<T> parent, T behavior, Identifier id, String title)
    {
        super(parent, behavior, id, title);
    }
}
