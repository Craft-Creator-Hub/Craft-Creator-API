package fr.en0ri4n.craftcreator.impl.model.container.minecraft;

import fr.en0ri4n.craftcreator.api.ui.screen.CoreContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CraftingTableRCBehavior;
import fr.en0ri4n.craftcreator.impl.model.screen.minecraft.CraftingTableRCScreenDefinition;
import lombok.Getter;

@Getter
public class CraftingTableRecipeCreatorContainerModel extends ContainerModel<CraftingTableRCBehavior>
{
    private final ContainerLayout layout;
    private final CraftingTableRCScreenDefinition screen;

    public CraftingTableRecipeCreatorContainerModel()
    {
        ContainerLayout layout = new ContainerLayout();
        int idx = 0;
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                layout.addSlot(new SlotDescriptor(SlotDescriptor.SlotType.RECIPE_CREATOR_INPUT, 30 + j * 18, 17 + i * 18, idx++, null));

        layout.addSlot(new SlotDescriptor(SlotDescriptor.SlotType.RECIPE_CREATOR_OUTPUT, 124, 35, idx++, null));
        addPlayerInventorySlots(layout, 8, 84);

        this.layout = layout;
        this.screen = new CraftingTableRCScreenDefinition(this);
    }

    @Override
    public ContainerLayout getLayout()
    {
        return layout;
    }

    @Override
    public CoreContainerScreenDefinition<CraftingTableRCBehavior> getScreenDefinition()
    {
        return screen;
    }
}