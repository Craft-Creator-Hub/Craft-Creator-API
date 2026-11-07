package fr.en0ri4n.craftcreator.platform.blockentity;

import com.google.gson.JsonParser;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.platform.item.ForgeItemStackAdapter;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import fr.en0ri4n.craftcreator.utils.Identifier;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Generic Forge BlockEntity that stores core block entity JSON in NBT and delegates behavior.
 * Use a single BlockEntityType for multiple core block-entity types.
 */
@Getter
public class ForgeGenericBlockEntity extends BaseContainerBlockEntity
{
    private static final String CORE_TAG = "core_json";

    private CoreBlockEntity coreEntity;

    public ForgeGenericBlockEntity(BlockPos pos, BlockState state)
    {
        super(ForgeRegistryAdapter.GENERIC_BLOCK_ENTITY.get(), pos, state);
    }

    public ForgeGenericBlockEntity(BlockPos pos, BlockState state, Identifier coreId)
    {
        this(pos, state);
        this.coreEntity = CoreBlockEntityManager.get().create(coreId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(CORE_TAG)) {
            String json = tag.getString(CORE_TAG);
            try {
                com.google.gson.JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                Identifier typeId = Identifier.from(obj.get("type").getAsString());
                CoreBlockEntityDefinition def = CoreBlockEntityManager.get().getDefinition(typeId);
                if (def != null) {
                    this.coreEntity = CoreBlockEntity.fromJson(obj, def);
                }
            } catch (Exception e) {
                CraftCreator.LOGGER.error("Failed to load core block entity from JSON: {}", json, e);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (coreEntity != null) {
            String s = coreEntity.toJson().toString();
            tag.putString(CORE_TAG, s);
        }
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.craftcreator." + getCoreEntity().getTypeId().getPath());
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), pInventory.player.registryAccess());
        buf.writeBlockPos(worldPosition);
        buf.writeUtf(getCoreEntity().getTypeId().toString());
        return new ForgeRecipeCreatorMenu(pContainerId, pInventory, buf);
    }

    /**
     * The slots live in the core block entity: this list is a copy, made for the base class.
     */
    @Override
    protected NonNullList<ItemStack> getItems()
    {
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        for(int i = 0; i < items.size(); i++)
            items.set(i, getItem(i));
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items)
    {
        for(int i = 0; i < Math.min(items.size(), getContainerSize()); i++)
            setItem(i, items.get(i));
    }

    @Override
    public int getContainerSize()
    {
        return getCoreEntity().getInventorySize();
    }

    @Override
    public boolean isEmpty()
    {
        return getCoreEntity().isInventoryEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot)
    {
        return ForgeItemStackAdapter.get().toPlatform(getCoreEntity().getSlot(pSlot));
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount)
    {
        return ForgeItemStackAdapter.get().toPlatform(getCoreEntity().removeItem(pSlot, pAmount));
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot)
    {
        return ForgeItemStackAdapter.get().toPlatform(getCoreEntity().removeItemNoUpdate(pSlot));
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack)
    {
        getCoreEntity().setSlot(pSlot, ForgeItemStackAdapter.get().fromPlatform(pStack));
    }

    @Override
    public boolean stillValid(Player pPlayer)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
        getCoreEntity().clearInventory();
    }
}
