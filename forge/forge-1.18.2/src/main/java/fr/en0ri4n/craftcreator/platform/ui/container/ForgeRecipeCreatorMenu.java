package fr.en0ri4n.craftcreator.platform.ui.container;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.impl.model.container.minecraft.CraftingTableRecipeCreatorContainerModel;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class ForgeRecipeCreatorMenu extends AbstractContainerMenu {

    private final ContainerModel model;

    public ForgeRecipeCreatorMenu(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv, new CraftingTableRecipeCreatorContainerModel());
    }

    public ForgeRecipeCreatorMenu(int windowId, Inventory playerInv, ContainerModel model) {
        super(ForgeRegistryAdapter.RECIPE_CREATOR_MENU.get(), windowId);
        this.model = model;
        ContainerLayout layout = model.getLayout();

        for (SlotDescriptor desc : layout.getSlots()) {
            switch (desc.getType()) {
                case PLAYER:
                case HOTBAR:
                    // map to player inventory indices
                    this.addSlot(new Slot(playerInv, desc.getIndex(), desc.getX(), desc.getY()));
                    break;
                case RECIPE_CREATOR_INPUT:
                    break;
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}