package fr.en0ri4n.craftcreator.recipe.creator;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.mod.SupportedMod;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeType;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializer;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Supplier;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeCreator<T extends RecipeCreatorBlockEntityBehavior>
{
    private final Identifier id;
    private final SupportedMod mod;
    private final List<SupportedRecipeType> recipeTypes;
    /** Recipe type ids handled by this creator, e.g. "minecraft:smelting" or "botania:mana_infusion". */
    private final List<Identifier> recipeTypeIds;
    private final RecipeSerializer serializer;
    private final CoreBlockDef recipeCreatorBlock;
    private final Supplier<T> behaviorFactory;
    private final CoreBlockEntityDefinition blockEntityDefinition;
    private final ContainerModel<T> containerModel;

    public boolean handlesRecipeType(Identifier recipeTypeId)
    {
        return recipeTypeIds.stream().anyMatch(id -> id.toString().equals(recipeTypeId.toString()));
    }

    public static class Builder<T extends RecipeCreatorBlockEntityBehavior>
    {
        private final Identifier id;
        private final Supplier<T> behavior;
        private SupportedMod mod = SupportedMod.MINECRAFT;
        private List<SupportedRecipeType> recipeTypes = List.of();
        private List<Identifier> recipeTypeIds;
        private RecipeSerializer serializer;
        private CoreBlockDef recipeCreatorBlock;
        private CoreBlockEntityDefinition blockEntityDefinition;
        private ContainerModel<T> containerModel;

        public Builder(Identifier id, Supplier<T> behavior)
        {
            this.id = id;
            this.behavior = behavior;
        }

        public Builder<T> setMod(SupportedMod mod) {
            this.mod = mod;
            return this;
        }

        public Builder<T> setRecipeTypes(SupportedRecipeType... recipeTypes) {
            this.recipeTypes = List.of(recipeTypes);
            return this;
        }

        /**
         * Recipe types not listed in {@link SupportedRecipeType} (e.g. other mods' types). Defaults to the ids of {@link #setRecipeTypes}.
         */
        public Builder<T> setRecipeTypeIds(Identifier... recipeTypeIds) {
            this.recipeTypeIds = List.of(recipeTypeIds);
            return this;
        }

        public Builder<T> setSerializer(RecipeSerializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder<T> setRecipeCreatorBlock(CoreBlockDef recipeCreatorBlock) {
            this.recipeCreatorBlock = recipeCreatorBlock;
            return this;
        }

        public Builder<T> setBlockEntityDefinition(CoreBlockEntityDefinition blockEntityDefinition) {
            this.blockEntityDefinition = blockEntityDefinition;
            return this;
        }

        public Builder<T> setContainerModel(ContainerModel<T> containerModel) {
            this.containerModel = containerModel;
            return this;
        }

        public RecipeCreator<T> build() {
            List<Identifier> typeIds = recipeTypeIds != null ? recipeTypeIds : recipeTypes.stream().map(type -> Identifier.from(type.getId())).toList();
            return new RecipeCreator<>(id, mod, recipeTypes, typeIds, serializer, recipeCreatorBlock, behavior, blockEntityDefinition, containerModel);
        }
    }
}
