package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.CraftCreator;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.platform.RegistryAdapter;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockItemDef;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreItemDef;
import fr.en0ri4n.craftcreator.platform.block.RecipeCreatorBlock;
import fr.en0ri4n.craftcreator.platform.blockentity.ForgeGenericBlockEntity;
import fr.en0ri4n.craftcreator.platform.ui.container.ForgeRecipeCreatorMenu;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public final class ForgeRegistryAdapter implements RegistryAdapter
{
    private static final ForgeRegistryAdapter instance = new ForgeRegistryAdapter();

    public static ForgeRegistryAdapter getInstance()
    {
        return instance;
    }

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CCReferences.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CCReferences.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, CCReferences.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CCReferences.MOD_ID);

    public static RegistryObject<MenuType<ForgeRecipeCreatorMenu>> RECIPE_CREATOR_MENU;
    public static RegistryObject<BlockEntityType<ForgeGenericBlockEntity>> GENERIC_BLOCK_ENTITY;

    private final List<RegistryObject<Block>> registeredBlocksWithEntity = new ArrayList<>();

    /**
     * Expose the DeferredRegister instances so caller can register them on their mod event bus.
     */
    public DeferredRegister<Block> getBlockDeferredRegister()
    {
        return BLOCKS;
    }

    public DeferredRegister<Item> getItemDeferredRegister()
    {
        return ITEMS;
    }

    public DeferredRegister<MenuType<?>> getMenuDeferredRegister()
    {
        return MENUS;
    }

    public DeferredRegister<BlockEntityType<?>> getBlockEntityDeferredRegister()
    {
        return BLOCK_ENTITIES;
    }

    @Override
    public void registerBlockItem(CoreBlockItemDef blockItemDef)
    {
        CoreBlockDef blockDef = blockItemDef.getBlock();
        ResourceLocation rl = toRL(blockDef.getId());
        String name = rl.getPath();

        // Register block first
        RegistryObject<Block> regBlock = BLOCKS.register(name, () -> new RecipeCreatorBlock(blockDef));

        registeredBlocksWithEntity.add(regBlock);

        // If the combined def includes an item, register a BlockItem that points to the registered block.
        if(blockItemDef.hasItem())
        {
            CoreItemDef itemDef = blockItemDef.getItem();
            Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS);
            if(itemDef.getMaxStackSize() != 64) props.stacksTo(itemDef.getMaxStackSize());

            ITEMS.register(name, () -> new BlockItem(regBlock.get(), props));
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

    private ResourceLocation toRL(Identifier id)
    {
        return CraftCreator.getInstance().getPlatform().getIdentifierAdapter().fromCore(id);
    }
}