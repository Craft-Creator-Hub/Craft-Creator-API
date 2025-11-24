package fr.en0ri4n.craftcreator.impl.model.container.minecraft;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.FurnaceRCBehavior;
import fr.en0ri4n.craftcreator.impl.model.screen.minecraft.FurnaceRCScreenDefinition;
import lombok.Getter;

@Getter
public class FurnaceRCContainerModel extends ContainerModel<FurnaceRCBehavior>
{
    private final ContainerLayout layout;
    private final FurnaceRCScreenDefinition screen;

    public FurnaceRCContainerModel()
    {
        ContainerLayout layout = new ContainerLayout();
        int idx = 0;
        layout.addSlot(new SlotDescriptor(SlotDescriptor.SlotType.RECIPE_CREATOR_INPUT, 56, 35, idx++, null));
        layout.addSlot(new SlotDescriptor(SlotDescriptor.SlotType.RECIPE_CREATOR_OUTPUT, 116, 35, idx++, null));
        addPlayerInventorySlots(layout, 8, 84);

        this.layout = layout;
        this.screen = new FurnaceRCScreenDefinition(this);
    }

    @Override
    public ContainerLayout getLayout()
    {
        return layout;
    }

    @Override
    public CoreContainerScreenDefinition<FurnaceRCBehavior> getScreenDefinition()
    {
        return screen;
    }
}