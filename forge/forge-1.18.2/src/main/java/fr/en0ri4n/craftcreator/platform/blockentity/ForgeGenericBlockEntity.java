package fr.en0ri4n.craftcreator.platform.blockentity;

import com.google.gson.JsonParser;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityManager;
import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityBehavior;
import fr.en0ri4n.craftcreator.platform.adapters.ForgeRegistryAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

/**
 * Generic Forge BlockEntity that stores core block entity JSON in NBT and delegates behavior.
 * Use a single BlockEntityType for multiple core block-entity types.
 */
public class ForgeGenericBlockEntity extends BlockEntity implements BlockEntityTicker<ForgeGenericBlockEntity>
{

    private static final String CORE_TAG = "core_json";

    private CoreBlockEntity coreEntity;

    public ForgeGenericBlockEntity(BlockPos pos, BlockState state) {
        super(ForgeRegistryAdapter.GENERIC_BLOCK_ENTITY.get(), pos, state);
    }

    public void setCoreEntity(CoreBlockEntity entity) {
        this.coreEntity = entity;
    }

    public CoreBlockEntity getCoreEntity() {
        return this.coreEntity;
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
                    // call behaviors onLoad via manager behaviorRegistry if needed
                    for (String bId : def.getBehaviors()) {
                        var sup = CoreBlockEntityManager.get().getBehaviorRegistry().get(bId);
                        if (sup != null) {
                            BlockEntityBehavior beh = sup.get();
                            beh.load(coreEntity, obj);
                            beh.onLoad(coreEntity, new ForgeBlockEntityContext(level, worldPosition, null));
                        }
                    }
                }
            } catch (Exception e) {
                // log parsing error if you have a logger
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
    public void setRemoved() {
        // call behaviors onRemove
        if (coreEntity != null) {
            CoreBlockEntityDefinition def = CoreBlockEntityManager.get().getDefinition(coreEntity.getTypeId());
            if (def != null) {
                for (String bId : def.getBehaviors()) {
                    var sup = CoreBlockEntityManager.get().getBehaviorRegistry().get(bId);
                    if (sup != null) sup.get().onRemove(coreEntity, new ForgeBlockEntityContext(level, worldPosition, null));
                }
            }
        }
        super.setRemoved();
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState, ForgeGenericBlockEntity pBlockEntity)
    {
        if (level == null || coreEntity == null) return;
        CoreBlockEntityDefinition def = CoreBlockEntityManager.get().getDefinition(coreEntity.getTypeId());
        if (def == null) return;

        // server-only ticking for now
        if (!level.isClientSide()) {
            ForgeBlockEntityContext ctx = new ForgeBlockEntityContext(level, worldPosition, null);
            for (String bId : def.getBehaviors()) {
                var sup = CoreBlockEntityManager.get().getBehaviorRegistry().get(bId);
                if (sup != null) {
                    BlockEntityBehavior beh = sup.get();
                    beh.tick(coreEntity, ctx);
                }
            }
        }
    }

    /**
     * Helper to handle interaction; called from block.use.
     */
    public boolean onInteract(ServerPlayer player) {
        if (coreEntity == null) return false;
        CoreBlockEntityDefinition def = CoreBlockEntityManager.get().getDefinition(coreEntity.getTypeId());
        if (def == null) return false;
        ForgeBlockEntityContext ctx = new ForgeBlockEntityContext(level, worldPosition, player);
        boolean handled = false;
        for (String bId : def.getBehaviors()) {
            var sup = CoreBlockEntityManager.get().getBehaviorRegistry().get(bId);
            if (sup != null) {
                BlockEntityBehavior beh = sup.get();
                if (beh.onInteract(coreEntity, ctx)) {
                    handled = true;
                }
            }
        }

        // if not handled by behavior, try to open a menu if block entity defines a container id in extra data
        if (!handled) {
            // example: coreEntity.extraData may contain "container" : "craftcreator:recipe_editor"
            if (coreEntity.getExtraData().has("container")) {
                String cid = coreEntity.getExtraData().get("container").getAsString();
                // open server-side container using NetworkHooks if you have a provider
                // For demonstration we send a simple MenuProvider that creates a menu using a ContainerModel
                NetworkHooks.openGui(player, new MenuProvider() {

                    @Override
                    public net.minecraft.network.chat.Component getDisplayName() {
                        return new TextComponent(cid);
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player playerEntity) {
                        // create a menu using your platform adapter / ContainerModel here
                        return null; // TODO: integrate your container factory
                    }
                }, buf -> buf.writeBlockPos(worldPosition));
                handled = true;
            }
        }

        return handled;
    }
}