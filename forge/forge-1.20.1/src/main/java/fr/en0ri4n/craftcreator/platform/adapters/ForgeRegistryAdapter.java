package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;
import fr.en0ri4n.craftcreator.api.platform.RegistryAdapter;
import fr.en0ri4n.craftcreator.platform.Forge1201Platform;
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
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ForgeRegistryAdapter implements RegistryAdapter
{

    public static ForgeRegistryAdapter get()
    {
        return INSTANCE;
    }

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CCReferences.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CCReferences.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CCReferences.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CCReferences.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CCReferences.MOD_ID);

    // Created after the registers above: its fields register the creative tab, which needs them
    private static final ForgeRegistryAdapter INSTANCE = new ForgeRegistryAdapter();

    public static RegistryObject<MenuType<ForgeRecipeCreatorMenu>> RECIPE_CREATOR_MENU;
    public static RegistryObject<BlockEntityType<ForgeGenericBlockEntity>> GENERIC_BLOCK_ENTITY;

    private final List<RegistryObject<Block>> registeredBlocksWithEntity = new ArrayList<>();
    private final List<RegistryObject<Item>> registeredItems = new ArrayList<>();

    /**
     * Creative tabs are registered since 1.19.3: every recipe creator goes in Craft Creator's own tab.
     */
    private final RegistryObject<CreativeModeTab> creativeTab = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + CCReferences.MOD_ID))
            .icon(() -> registeredItems.isEmpty() ? ItemStack.EMPTY : new ItemStack(registeredItems.get(0).get()))
            .displayItems((parameters, output) -> registeredItems.forEach(item -> output.accept(item.get())))
            .build());

    public List<Block> getRegisteredBlocks()
    {
        return registeredBlocksWithEntity.stream().map(RegistryObject::get).toList();
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
        RegistryObject<Block> regBlock = BLOCKS.register(name, () -> new RecipeCreatorBlock(blockDef));

        // Keep track of blocks that have an associated block entity
        registeredBlocksWithEntity.add(regBlock);

        // If the combined def includes an item, register a BlockItem that points to the registered block.
        if(blockItemDef.hasItem())
        {
            CoreItemDef itemDef = blockItemDef.getItem();
            Item.Properties props = new Item.Properties();
            if(itemDef.getMaxStackSize() != 64) props.stacksTo(itemDef.getMaxStackSize());

            RegistryObject<Item> item = ITEMS.register(name, () -> new BlockItem(regBlock.get(), props));
            // Recipe creators for mods that aren't installed are left out of the creative tab
            if(RecipeCreators.isBlockAvailable(blockDef.getId())) registeredItems.add(item);
        }
    }

    @Override
    public void registerMenus()
    {
        RECIPE_CREATOR_MENU = MENUS.register("recipe_creator", () -> IForgeMenuType.create(ForgeRecipeCreatorMenu::new));
    }

    @Override
    public void registerBlockEntities()
    {
        GENERIC_BLOCK_ENTITY = BLOCK_ENTITIES.register("generic_block_entity", () ->
                        BlockEntityType.Builder.of(ForgeGenericBlockEntity::new, registeredBlocksWithEntity
                                        .stream()
                                        .map(RegistryObject::get)
                                        .toArray(Block[]::new))
                                .build(null));
    }

    @Override
    public void registerPackets()
    {
        NetworkHandler.registerPackets();
    }

    private ResourceLocation toRL(Identifier id)
    {
        return Forge1201Platform.get().getIdentifierAdapter().fromCore(id);
    }
}