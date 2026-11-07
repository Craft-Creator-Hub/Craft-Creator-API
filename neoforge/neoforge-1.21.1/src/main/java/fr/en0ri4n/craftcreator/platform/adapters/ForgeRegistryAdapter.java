package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;
import fr.en0ri4n.craftcreator.api.platform.RegistryAdapter;
import fr.en0ri4n.craftcreator.platform.NeoForge1211Platform;
import fr.en0ri4n.craftcreator.platform.block.RecipeCreatorBlock;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.net.NetworkHandler;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

public class ForgeRegistryAdapter implements RegistryAdapter
{

    public static ForgeRegistryAdapter get()
    {
        return INSTANCE;
    }

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, CCReferences.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CCReferences.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, CCReferences.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CCReferences.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CCReferences.MOD_ID);

    // Created after the registers above: its fields register the creative tab, which needs them
    private static final ForgeRegistryAdapter INSTANCE = new ForgeRegistryAdapter();

    public static DeferredHolder<MenuType<?>, MenuType<ForgeRecipeCreatorMenu>> RECIPE_CREATOR_MENU;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ForgeGenericBlockEntity>> GENERIC_BLOCK_ENTITY;

    private final List<DeferredHolder<Block, Block>> registeredBlocksWithEntity = new ArrayList<>();
    private final List<DeferredHolder<Item, Item>> registeredItems = new ArrayList<>();

    /**
     * Creative tabs are registered since 1.19.3: every recipe creator goes in Craft Creator's own tab.
     */
    private final DeferredHolder<CreativeModeTab, CreativeModeTab> creativeTab = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + CCReferences.MOD_ID))
            .icon(() -> registeredItems.isEmpty() ? ItemStack.EMPTY : new ItemStack(registeredItems.get(0).get()))
            .displayItems((parameters, output) -> registeredItems.forEach(item -> output.accept(item.get())))
            .build());

    public List<Block> getRegisteredBlocks()
    {
        return registeredBlocksWithEntity.stream().map(DeferredHolder::get).toList();
    }

    public void registerDefferredRegisters(IEventBus bus)
    {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        MENUS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CREATIVE_TABS.register(bus);
    }

    @Override
    public void registerBlockItem(CoreBlockItemDef blockItemDef)
    {
        CoreBlockDef blockDef = blockItemDef.getBlock();
        ResourceLocation rl = toRL(blockDef.getId());
        String name = rl.getPath();

        // Register block first
        DeferredHolder<Block, Block> regBlock = BLOCKS.register(name, () -> new RecipeCreatorBlock(blockDef));

        // Keep track of blocks that have an associated block entity
        registeredBlocksWithEntity.add(regBlock);

        // If the combined def includes an item, register a BlockItem that points to the registered block.
        if(blockItemDef.hasItem())
        {
            CoreItemDef itemDef = blockItemDef.getItem();
            Item.Properties props = new Item.Properties();
            if(itemDef.getMaxStackSize() != 64) props.stacksTo(itemDef.getMaxStackSize());

            registeredItems.add(ITEMS.register(name, () -> new BlockItem(regBlock.get(), props)));
        }
    }

    @Override
    public void registerMenus()
    {
        RECIPE_CREATOR_MENU = MENUS.register("recipe_creator", () -> IMenuTypeExtension.create(ForgeRecipeCreatorMenu::new));
    }

    @Override
    public void registerBlockEntities()
    {
        GENERIC_BLOCK_ENTITY = BLOCK_ENTITIES.register("generic_block_entity", () ->
                        BlockEntityType.Builder.of(ForgeGenericBlockEntity::new, registeredBlocksWithEntity
                                        .stream()
                                        .map(DeferredHolder::get)
                                        .toArray(Block[]::new))
                                .build(null));
    }

    @Override
    public void registerPackets()
    {
        // Payloads are registered with RegisterPayloadHandlersEvent, see CraftCreator
    }

    private ResourceLocation toRL(Identifier id)
    {
        return NeoForge1211Platform.get().getIdentifierAdapter().fromCore(id);
    }
}