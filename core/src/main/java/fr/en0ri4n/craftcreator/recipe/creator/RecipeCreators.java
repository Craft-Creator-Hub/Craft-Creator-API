package fr.en0ri4n.craftcreator.recipe.creator;


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
    private static final Map<SlotRecipeType, CoreVoxelShape> SLOT_RECIPE_SHAPES = Map.of(
            // Minecraft
            SlotRecipeTypes.CRAFTING_TABLE, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_NORTH,
            SlotRecipeTypes.STONECUTTING, CoreShapes.MinecraftRecipeCreatorShapes.SHAPE_STONECUTTER,
            // Botania
            SlotRecipeTypes.MANA_INFUSION, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_MANA_POOL,
            SlotRecipeTypes.PURE_DAISY, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_PURE_DAISY,
            SlotRecipeTypes.BREWERY, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_BREWERY,
            SlotRecipeTypes.PETAL_APOTHECARY, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_PETAL_APOTHECARY,
            SlotRecipeTypes.RUNIC_ALTAR, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_RUNIC_ALTAR,
            SlotRecipeTypes.TERRA_PLATE, CoreShapes.BotaniaRecipeCreatorShapes.SHAPE_TERRA_PLATE,
            // Create
            SlotRecipeTypes.CUTTING, CoreShapes.CreateRecipeCreatorShapes.SHAPE_MECHANICAL_SAW);

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

    public static void registerAll()
    {
        // Force class loading
    }
}