package fr.en0ri4n.craftcreator.platform.ui.container;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class ForgeRecipeCreatorMenu extends AbstractContainerMenu {

    private final ContainerModel model;

    public ForgeRecipeCreatorMenu(int windowId, Inventory playerInv, ContainerModel model) {
        super(MenuType.ANVIL, windowId);
        this.model = model;
        ContainerLayout layout = model.getLayout();

        // Example: create Slots based on SlotDescriptor.
        for (SlotDescriptor desc : layout.getSlots()) {
            switch (desc.getType()) {
                case PLAYER:
                case HOTBAR:
                    // map to player inventory indices
                    this.addSlot(new Slot(playerInv, desc.getIndex(), desc.getX(), desc.getY()));
                    break;
                case CUSTOM:
                case GHOST:
                    // create a custom Slot implementation that delegates to model
//                    this.addSlot(new ModelBackedSlot(model, desc));
                    break;
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // You override click handling and delegate to model:
    @Override
    public void clicked(int slotId, int button, net.minecraft.world.inventory.ClickType clickType, Player player) {
        super.clicked(slotId, button, clickType, player);
        if (slotId >= 0 && slotId < slots.size()) {
            Slot slot = slots.get(slotId);
//            if (slot instanceof ModelBackedSlot mbs) {
//                Identifier id = mbs.getDescriptor().getId();
//                model.onSlotClick(id, button, clickType == net.minecraft.world.inventory.ClickType.QUICK_MOVE);
//            }
        }
    }
}