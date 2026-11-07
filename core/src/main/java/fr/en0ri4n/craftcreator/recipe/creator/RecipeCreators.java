package fr.en0ri4n.craftcreator.recipe.creator;


import fr.en0ri4n.craftcreator.api.mod.SupportedMod;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.init.definitions.FacingType;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreShapes;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreVoxelShape;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.SlotRecipeBehavior;
import fr.en0ri4n.craftcreator.impl.model.container.SlotRecipeContainerModel;
import fr.en0ri4n.craftcreator.recipe.serialize.SlotRecipeSerializer;
import fr.en0ri4n.craftcreator.recipe.slot.SlotRecipeType;
import fr.en0ri4n.craftcreator.recipe.slot.SlotRecipeTypes;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeCreators
{
    public static final List<RecipeCreator<?>> ALL_RECIPE_CREATORS = new ArrayList<>();

    /**
     * Shape (collision and outline) of slot recipe blocks whose model isn't a full cube, given facing north:
     * it is rotated with the block. Types not listed here are full blocks.
     */
    private static final Map<SlotRecipeType, CoreVoxelShape> SLOT_RECIPE_SHAPES = Map.ofEntries(
            // Minecraft
            Map.entry(SlotRecipeTypes.CRAFTING_TABLE, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_NORTH),
            Map.entry(SlotRecipeTypes.STONECUTTING, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_STONECUTTER),
            // Botania
            Map.entry(SlotRecipeTypes.MANA_INFUSION, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_MANA_POOL),
            Map.entry(SlotRecipeTypes.PURE_DAISY, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_PURE_DAISY),
            Map.entry(SlotRecipeTypes.BREWERY, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_BREWERY),
            Map.entry(SlotRecipeTypes.PETAL_APOTHECARY, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_PETAL_APOTHECARY),
            Map.entry(SlotRecipeTypes.RUNIC_ALTAR, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_RUNIC_ALTAR),
            Map.entry(SlotRecipeTypes.TERRA_PLATE, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_TERRA_PLATE),
            // Thermal
            Map.entry(SlotRecipeTypes.STIRLING_FUEL, CoreShapes.ThermalRecipeCreatorShapes.SHAPE_DYNAMO),
            Map.entry(SlotRecipeTypes.COMPRESSION_FUEL, CoreShapes.ThermalRecipeCreatorShapes.SHAPE_DYNAMO),
            Map.entry(SlotRecipeTypes.MAGMATIC_FUEL, CoreShapes.ThermalRecipeCreatorShapes.SHAPE_DYNAMO),
            Map.entry(SlotRecipeTypes.NUMISMATIC_FUEL, CoreShapes.ThermalRecipeCreatorShapes.SHAPE_DYNAMO),
            Map.entry(SlotRecipeTypes.LAPIDARY_FUEL, CoreShapes.ThermalRecipeCreatorShapes.SHAPE_DYNAMO),
            Map.entry(SlotRecipeTypes.DISENCHANTMENT_FUEL, CoreShapes.ThermalRecipeCreatorShapes.SHAPE_DYNAMO),
            Map.entry(SlotRecipeTypes.GOURMAND_FUEL, CoreShapes.ThermalRecipeCreatorShapes.SHAPE_DYNAMO),
            // Create
            Map.entry(SlotRecipeTypes.CUTTING, CoreShapes.CreateRecipeCreatorShapes.SHAPE_MECHANICAL_SAW),
            Map.entry(SlotRecipeTypes.MILLING, CoreShapes.CreateRecipeCreatorShapes.SHAPE_MILLSTONE),
            Map.entry(SlotRecipeTypes.DEPLOYING, CoreShapes.CreateRecipeCreatorShapes.SHAPE_DEPLOYER),
            Map.entry(SlotRecipeTypes.FILLING, CoreShapes.CreateRecipeCreatorShapes.SHAPE_SPOUT),
            Map.entry(SlotRecipeTypes.EMPTYING, CoreShapes.CreateRecipeCreatorShapes.SHAPE_DEPOT),
            Map.entry(SlotRecipeTypes.SANDPAPER_POLISHING, CoreShapes.CreateRecipeCreatorShapes.SHAPE_DEPOT),
            Map.entry(SlotRecipeTypes.SEQUENCED_ASSEMBLY, CoreShapes.CreateRecipeCreatorShapes.SHAPE_DEPOT));

    static {
        SlotRecipeTypes.ALL.forEach(type -> ALL_RECIPE_CREATORS.add(slotRecipeCreator(type)));
    }

    /**
     * One block per slot recipe type, sharing the type's id.
     */
    private static RecipeCreator<SlotRecipeBehavior> slotRecipeCreator(SlotRecipeType type)
    {
        return new RecipeCreator.Builder<>(type.getId(), () -> new SlotRecipeBehavior(type))
                .setMod(type.getMod())
                .setRecipeTypeIds(type.getRecipeTypeIds())
                .setRecipeCreatorBlock(slotRecipeBlock(type))
                .setBlockEntityDefinition(CoreBlockEntityDefinition.builder(type.getId())
                                                  .inventorySize(type.getSlots().size())
                                                  .setBehavior(type.getId())
                                                  .build())
                .setContainerModel(new SlotRecipeContainerModel(type))
                .setSerializer(new SlotRecipeSerializer(type))
                .build();
    }

    private static CoreBlockDef slotRecipeBlock(SlotRecipeType type)
    {
        CoreBlockDef.Builder block = CoreBlockDef.builder(type.getId()).facing(FacingType.HORIZONTAL);
        CoreVoxelShape shape = SLOT_RECIPE_SHAPES.get(type);
        if(shape != null) block.facingShapes(CoreVoxelShape.horizontal(shape));
        return block.build();
    }

    /**
     * @return the recipe creator able to make recipes of this type (e.g. "botania:mana_infusion"), or null
     */
    public static RecipeCreator<?> getRecipeCreator(Identifier recipeTypeId)
    {
        return ALL_RECIPE_CREATORS.stream().filter(rc -> rc.handlesRecipeType(recipeTypeId)).findFirst().orElse(null);
    }

    /**
     * @return the mod whose recipes the recipe creator block with this id makes, or null if no recipe creator has this id
     */
    public static SupportedMod getModOfBlock(Identifier blockId)
    {
        return ALL_RECIPE_CREATORS.stream().filter(rc -> rc.getId().equals(blockId)).map(RecipeCreator::getMod).findFirst().orElse(null);
    }

    /**
     * A recipe creator block is usable when the mod it makes recipes for is installed (always for Minecraft's).
     * Blocks of missing mods stay registered, so worlds keep them, but they're left out of the creative tab and don't open.
     */
    public static boolean isBlockAvailable(Identifier blockId)
    {
        SupportedMod mod = getModOfBlock(blockId);
        return mod == null || mod == SupportedMod.MINECRAFT || CraftCreatorAPI.get().getPlatform().getServices().IsModLoaded(mod);
    }

    /**
     * @return the name of the mod the block needs, e.g. "Create", for messages
     */
    public static String getRequiredModName(Identifier blockId)
    {
        SupportedMod mod = getModOfBlock(blockId);
        if(mod == null) return "";
        String id = mod.getModId();
        return Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }

    public static void registerAll()
    {
        // Force class loading
    }
}