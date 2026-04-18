package fr.en0ri4n.craftcreator.platform.ui.container;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.impl.model.ContainerModels;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Getter
public class ForgeRecipeCreatorMenu extends AbstractContainerMenu
{

    private final ContainerModel<?> model;
    private final ForgeGenericBlockEntity coreEntity;
    private final BlockPos pos;

    public ForgeRecipeCreatorMenu(int windowId, Inventory playerInv, FriendlyByteBuf buf)
    {
        this(windowId, playerInv, buf.readBlockPos(), Identifier.from(buf.readUtf()));
    }

    public ForgeRecipeCreatorMenu(int windowId, Inventory playerInv, BlockPos pos, Identifier modelId)
    {
        super(ForgeRegistryAdapter.RECIPE_CREATOR_MENU.get(), windowId);
        this.model = ContainerModels.get().getContainerModel(modelId, Forge1182Platform.get().getBlockPosAdapter().toCore(pos));
        this.coreEntity = (ForgeGenericBlockEntity) playerInv.player.level.getBlockEntity(pos);
        this.pos = pos;
        ContainerLayout layout = model.getLayout();

        if(coreEntity == null)
        {
            throw new IllegalStateException("Block entity at " + pos + " is not a valid CoreBlockEntity");
        }

        for(SlotDescriptor desc : layout.getSlots())
        {
            switch(desc.getType())
            {
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

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = this.slots.get(index);

        if(slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        // Find player inventory slot range (PLAYER + HOTBAR slots)
        int playerStart = -1, playerEnd = -1;
        // Find block entity slot range (INPUT + OUTPUT slots)
        int blockStart = -1, blockEnd = -1;

        for(int i = 0; i < this.slots.size(); i++)
        {
            Slot s = this.slots.get(i);
            if(s.container == player.getInventory())
            {
                if(playerStart == -1) playerStart = i;
                playerEnd = i + 1;
            }
            else if(s.container == coreEntity)
            {
                if(blockStart == -1) blockStart = i;
                blockEnd = i + 1;
            }
        }

        if(playerStart == -1 || blockStart == -1) return ItemStack.EMPTY;

        // Only allow shift-click FROM block entity slots TO player inventory
        // Shift-clicking player inventory slots does nothing
        if(slot.container == coreEntity)
        {
            if(!this.moveItemStackTo(stack, playerStart, playerEnd, false))
            {
                return ItemStack.EMPTY;
            }
        }
        else
        {
            return ItemStack.EMPTY;
        }

        if(stack.isEmpty())
        {
            slot.set(ItemStack.EMPTY);
        }
        else
        {
            slot.setChanged();
        }

        return original;
    }
}