package fr.en0ri4n.craftcreator.recipe.creator;


import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeType;
import fr.en0ri4n.craftcreator.impl.InitManager;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CraftingTableRCBehavior;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.FurnaceRCBehavior;
import fr.en0ri4n.craftcreator.impl.blockentity.definitions.CoreBlockEntityDefinitionsRegistrar;
import fr.en0ri4n.craftcreator.impl.model.ContainerModels;
import fr.en0ri4n.craftcreator.recipe.serialize.CraftingTableRecipeSerializer;
import fr.en0ri4n.craftcreator.recipe.serialize.FurnaceRecipeSerializer;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RecipeCreators
{
    public static final Identifier CRAFTING_TABLE_RECIPE_CREATOR_ID = Identifier.fromMod("crafting_table_recipe_creator");
    public static final Identifier FURNACE_RECIPE_CREATOR_ID = Identifier.fromMod("furnace_recipe_creator");

    public static final RecipeCreator<CraftingTableRCBehavior> CRAFTING_TABLE_RECIPE_CREATOR;
    public static final RecipeCreator<FurnaceRCBehavior> FURNACE_RECIPE_CREATOR;

    public static final List<RecipeCreator<?>> ALL_RECIPE_CREATORS = new ArrayList<>();

    static {
        CRAFTING_TABLE_RECIPE_CREATOR = new RecipeCreator.Builder<>(CRAFTING_TABLE_RECIPE_CREATOR_ID, CraftingTableRCBehavior::new)
                .setBlockEntityDefinition(CoreBlockEntityDefinitionsRegistrar.CRAFTING_TABLE_RECIPE_CREATOR_DEFINITION)
                .setRecipeCreatorBlock(InitManager.CRAFTING_TABLE_RECIPE_CREATOR_BLOCK)
                .setContainerModel(ContainerModels.CRAFTING_TABLE_RC_CONTAINER_MODEL)
                .setSerializer(CraftingTableRecipeSerializer.get())
                .setRecipeTypes(SupportedRecipeType.CRAFTING_SHAPED, SupportedRecipeType.CRAFTING_SHAPELESS)
                .build();

        FURNACE_RECIPE_CREATOR = new RecipeCreator.Builder<>(FURNACE_RECIPE_CREATOR_ID, FurnaceRCBehavior::new)
                .setBlockEntityDefinition(CoreBlockEntityDefinitionsRegistrar.FURNACE_RECIPE_CREATOR_DEFINITION)
                .setRecipeCreatorBlock(InitManager.FURNACE_RECIPE_CREATOR_BLOCK)
                .setContainerModel(ContainerModels.FURNACE_RC_CONTAINER_MODEL)
                .setSerializer(FurnaceRecipeSerializer.get())
                .setRecipeTypes(SupportedRecipeType.FURNACE, SupportedRecipeType.SMOKER, SupportedRecipeType.BLAST_FURNACE)
                .build();

        ALL_RECIPE_CREATORS.addAll(List.of(
                CRAFTING_TABLE_RECIPE_CREATOR,
                FURNACE_RECIPE_CREATOR));
    }

    public static void registerAll()
    {
        // Force class loading
    }
}