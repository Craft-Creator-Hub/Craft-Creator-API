package fr.en0ri4n.craftcreator.api.block;

import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for block click handlers.
 *
 * {@code
 * @SubscribeEvent
 * public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
 *     var platform = CraftCreatorAPI.getInstance().getPlatform();
 *     var blockAdapter = (BlockAdapter<Block>) platform.getBlockAdapter();
 *     var ui = platform.getUiAdapter();
 *
 *     Block mcBlock = event.getWorld().getBlockState(event.getPos()).getBlock();
 *     CCBlock coreBlock = blockAdapter.fromPlatform(mcBlock);
 *     if (coreBlock == null) return;
 *
 *     BlockClickHandler handler = BlockClickRegistry.getHandler(coreBlock.getRegistryName());
 *     if (handler == null) return;
 *
 *     ClickContext ctx = ui.buildContext(event);
 *     ui.schedule(c -> handler.onClick(coreBlock, c), ctx);
 * }
 * }
 */
public final class BlockClickRegistry {

    private static final Map<Identifier, BlockClickHandler> HANDLERS = new HashMap<>();

    private BlockClickRegistry() {}

    public static void register(Identifier blockId, BlockClickHandler handler) {
        HANDLERS.put(blockId, handler);
    }

    public static BlockClickHandler getHandler(Identifier blockId) {
        return HANDLERS.get(blockId);
    }
}