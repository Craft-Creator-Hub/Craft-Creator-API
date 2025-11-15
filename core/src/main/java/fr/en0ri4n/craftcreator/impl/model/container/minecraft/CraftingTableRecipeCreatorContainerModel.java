package fr.en0ri4n.craftcreator.impl.model.container.minecraft;

import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.impl.model.screen.minecraft.CraftingTableScreenDefinition;
import lombok.Getter;

@Getter
public class CraftingTableRecipeCreatorContainerModel implements ContainerModel
{
    private final ContainerLayout layout;
    private final CoreScreenDefinition screen;

    public CraftingTableRecipeCreatorContainerModel()
    {
        this.layout = getLayout();
        this.screen = getScreenDefinition();
    }

    @Override
    public ContainerLayout getLayout()
    {
        ContainerLayout layout = new ContainerLayout();
        // Define slots specific to crafting table recipe creation
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                layout.addSlot(new SlotDescriptor(SlotDescriptor.SlotType.RECIPE_CREATOR_INPUT, 30 + j * 18, 17 + i * 18, i * 3 + j, null));
            }
        }

        layout.addSlot(new SlotDescriptor(SlotDescriptor.SlotType.RECIPE_CREATOR_OUTPUT, 124, 35, 0, null));

        ContainerModel.addPlayerInventorySlots(layout, 8, 84);

        return layout;
    }

    @Override
    public CoreScreenDefinition getScreenDefinition()
    {
        return new CraftingTableScreenDefinition();
    }

    @Override
    public void onButtonPressed(String elementId, String actionId)
    {
        screen.onButtonPressed(elementId, actionId);
    }

    @Override
    public void onDropdownChanged(String elementId, int index, String value)
    {
        screen.onDropdownChanged(elementId, index, value);
    }
}