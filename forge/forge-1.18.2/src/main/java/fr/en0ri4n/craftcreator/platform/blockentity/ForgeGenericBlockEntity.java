package fr.en0ri4n.craftcreator.platform.blockentity;

import com.google.gson.JsonParser;
import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityBehavior;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.platform.item.ForgeItemStackAdapter;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import fr.en0ri4n.craftcreator.utils.Identifier;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * Generic Forge BlockEntity that stores core block entity JSON in NBT and delegates behavior.
 * Use a single BlockEntityType for multiple core block-entity types.
 */
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
        coreEntity = CoreBlockEntityManager.get().create(coreId);
        coreEntity.getExtraData().addProperty("container", coreId.toString());
    }

    public CoreBlockEntity getCoreEntity() {
        return coreEntity;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(CORE_TAG)) {
            String json = tag.getString(CORE_TAG);
            try {
                com.google.gson.JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                Identifier typeId = Identifier.from(obj.get("type").getAsString());
                CoreBlockEntityDefinition def = CoreBlockEntityManager.get().getDefinition(typeId);
                if (def != null) {
                    this.coreEntity = CoreBlockEntity.fromJson(obj, def);
                    this.coreEntity.getBehavior().onLoad(this.coreEntity, new ForgeBlockEntityContext(level, worldPosition, null));
                }
            } catch (Exception e) {
                CraftCreator.LOGGER.error("Failed to load core block entity from JSON: {}", json, e);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (coreEntity != null) {
            String s = coreEntity.toJson().toString();
            tag.putString(CORE_TAG, s);
        }
    }

    @Override
    protected Component getDefaultName()
    {
        return new TranslatableComponent("container.craftcreator." + getCoreEntity().getTypeId().getPath());
    }

    @Override
    public void setRemoved() {
        // call behaviors onRemove
        if (coreEntity != null) {
            CoreBlockEntityDefinition def = CoreBlockEntityManager.get().getDefinition(coreEntity.getTypeId());
            if (def != null) {
                for (Identifier bId : def.getBehaviors()) {
                    Supplier<BlockEntityBehavior> sup = CoreBlockEntityManager.get().getBehavior(bId);
                    if (sup != null) sup.get().onRemove(coreEntity, new ForgeBlockEntityContext(level, worldPosition, null));
                }
            }
        }
        super.setRemoved();
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory)
    {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(worldPosition);
        buf.writeUtf(getCoreEntity().getTypeId().toString());
        return new ForgeRecipeCreatorMenu(pContainerId, pInventory, buf);
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