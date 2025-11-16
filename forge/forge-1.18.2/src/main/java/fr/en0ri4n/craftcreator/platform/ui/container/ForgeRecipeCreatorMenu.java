package fr.en0ri4n.craftcreator.platform.ui.container;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.blockentity.CoreItemStack;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.api.ui.recipe.RecipeCreatorContainerModel;
import fr.en0ri4n.craftcreator.impl.model.container.minecraft.CraftingTableRecipeCreatorContainerModel;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

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
                case CUSTOM:
                    // If this is a RecipeCreatorContainerModel, add slots bound to core entity
                    if (model instanceof RecipeCreatorContainerModel) {
                        RecipeCreatorContainerModel rcModel = (RecipeCreatorContainerModel) model;
                        this.addSlot(new CoreBackedSlot(rcModel.getCoreEntity(), desc.getIndex(), desc.getX(), desc.getY()));
                    }
                    break;
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Slot implementation that bridges between Minecraft ItemStack and CoreItemStack.
     * Stores items in the CoreBlockEntity's inventory.
     */
    private static class CoreBackedSlot extends Slot {
        private final CoreBlockEntity coreEntity;
        private final int slotIndex;

        public CoreBackedSlot(CoreBlockEntity coreEntity, int index, int x, int y) {
            super(null, index, x, y); // No IItemHandler, we manage ourselves
            this.coreEntity = coreEntity;
            this.slotIndex = index;
        }

        @Override
        @NotNull
        public ItemStack getItem() {
            CoreItemStack coreStack = coreEntity.getSlot(slotIndex);
            if (coreStack == null || coreStack.getCount() <= 0) {
                return ItemStack.EMPTY;
            }
            
            // Convert CoreItemStack to ItemStack
            Identifier itemId = coreStack.getItemId();
            ResourceLocation rl = new ResourceLocation(itemId.getNamespace(), itemId.getPath());
            net.minecraft.world.item.Item item = ForgeRegistries.ITEMS.getValue(rl);
            
            if (item == null) {
                return ItemStack.EMPTY;
            }
            
            return new ItemStack(item, coreStack.getCount());
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            if (stack.isEmpty()) {
                coreEntity.setSlot(slotIndex, new CoreItemStack(Identifier.from("minecraft:air"), 0));
            } else {
                ResourceLocation rl = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (rl != null) {
                    Identifier itemId = Identifier.from(rl.getNamespace(), rl.getPath());
                    coreEntity.setSlot(slotIndex, new CoreItemStack(itemId, stack.getCount()));
                }
            }
            this.setChanged();
        }

        @Override
        public void onQuickCraft(@NotNull ItemStack oldStack, @NotNull ItemStack newStack) {
            // Handle quick crafting
            int diff = newStack.getCount() - oldStack.getCount();
            if (diff > 0) {
                this.onCrafted(newStack, diff);
            }
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return true; // Allow any item
        }

        @Override
        @NotNull
        public ItemStack remove(int amount) {
            CoreItemStack coreStack = coreEntity.getSlot(slotIndex);
            if (coreStack == null || coreStack.getCount() <= 0) {
                return ItemStack.EMPTY;
            }

            int toRemove = Math.min(amount, coreStack.getCount());
            Identifier itemId = coreStack.getItemId();
            
            // Update the core stack
            int remaining = coreStack.getCount() - toRemove;
            coreEntity.setSlot(slotIndex, new CoreItemStack(itemId, remaining));
            
            // Return the removed stack
            ResourceLocation rl = new ResourceLocation(itemId.getNamespace(), itemId.getPath());
            net.minecraft.world.item.Item item = ForgeRegistries.ITEMS.getValue(rl);
            
            if (item == null) {
                return ItemStack.EMPTY;
            }
            
            this.setChanged();
            return new ItemStack(item, toRemove);
        }
    }
}