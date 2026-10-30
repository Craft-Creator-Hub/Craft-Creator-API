package fr.en0ri4n.craftcreator.impl.model.container;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.SlotRecipeBehavior;
import fr.en0ri4n.craftcreator.impl.model.screen.SlotRecipeScreenDefinition;
import fr.en0ri4n.craftcreator.recipe.slot.SlotRecipeType;
import fr.en0ri4n.craftcreator.recipe.slot.SlotSpec;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.List;

/**
 * Container of one {@link SlotRecipeType}: its slots use the first inventory indices of the recipe creator block entity.
 */
@Getter
public class SlotRecipeContainerModel extends ContainerModel<SlotRecipeBehavior>
{
    private final SlotRecipeType type;
    private final ContainerLayout layout;
    private final SlotRecipeScreenDefinition screen;

    public SlotRecipeContainerModel(SlotRecipeType type)
    {
        this.type = type;
        this.layout = new ContainerLayout().size(type.getGuiWidth(), type.getGuiHeight());

        List<SlotSpec> slots = type.getSlots();
        for(int i = 0; i < slots.size(); i++)
        {
            SlotSpec slot = slots.get(i);
            SlotDescriptor.SlotType slotType = slot.output() ? SlotDescriptor.SlotType.RECIPE_CREATOR_OUTPUT : SlotDescriptor.SlotType.RECIPE_CREATOR_INPUT;
            layout.addSlot(new SlotDescriptor(slotType, slot.x(), slot.y(), i, Identifier.fromMod(slot.name())));
        }
        addPlayerInventorySlots(layout, type.getInventoryX(), type.getInventoryY());

        this.screen = new SlotRecipeScreenDefinition(this);
    }

    @Override
    public CoreContainerScreenDefinition<SlotRecipeBehavior> getScreenDefinition()
    {
        return screen;
    }
}
