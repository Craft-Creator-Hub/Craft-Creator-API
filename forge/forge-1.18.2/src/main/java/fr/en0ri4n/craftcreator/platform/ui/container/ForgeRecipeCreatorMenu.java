package fr.en0ri4n.craftcreator.platform.ui.container;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.impl.model.ContainerModels;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ForgeRecipeCreatorMenu extends AbstractContainerMenu {

    private final ContainerModel<?> model;
    private final ForgeGenericBlockEntity coreEntity;
    private final BlockPos pos;

    public ForgeRecipeCreatorMenu(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv, buf.readBlockPos(), Identifier.from(buf.readUtf()));
    }

    public ForgeRecipeCreatorMenu(int windowId, Inventory playerInv, BlockPos pos, Identifier modelId) {
        super(ForgeRegistryAdapter.RECIPE_CREATOR_MENU.get(), windowId);
        this.model = ContainerModels.get().getContainerModel(modelId, Forge1182Platform.get().getBlockPosAdapter().toCore(pos));
        this.coreEntity = (ForgeGenericBlockEntity) playerInv.player.level.getBlockEntity(pos);
        this.pos = pos;
        ContainerLayout layout = model.getLayout();

        if(coreEntity == null) {
            throw new IllegalStateException("Block entity at " + pos + " is not a valid CoreBlockEntity");
        }

        for (SlotDescriptor desc : layout.getSlots()) {
            switch (desc.getType()) {
                case PLAYER:
                case HOTBAR:
                    // map to player inventory indices
                    this.addSlot(new Slot(playerInv, desc.getIndex(), desc.getX(), desc.getY()));
                    break;
                case RECIPE_CREATOR_INPUT:
                case RECIPE_CREATOR_OUTPUT:
                    this.addSlot(new Slot(coreEntity, desc.getIndex(), desc.getX(), desc.getY()));
                    break;
                case CUSTOM:
                    break;
            }
        }
    }

    public ContainerModel<?> getModel() {
        return model;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex)
    {
        return coreEntity.getItem(pIndex);
    }

    public BlockPos getPos()
    {
        return pos;
    }
}