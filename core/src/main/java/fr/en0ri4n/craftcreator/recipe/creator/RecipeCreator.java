package fr.en0ri4n.craftcreator.recipe.creator;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntityDefinition;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockDef;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeType;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializer;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Supplier;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RecipeCreator<T extends RecipeCreatorBlockEntityBehavior>
{
    private final Identifier id;
    private final List<SupportedRecipeType> recipeTypes;
    private final RecipeSerializer serializer;
    private final CoreBlockDef recipeCreatorBlock;
    private final Supplier<T> behaviorFactory;
    private final CoreBlockEntityDefinition blockEntityDefinition;
    private final ContainerModel<T> containerModel;

    public static class Builder<T extends RecipeCreatorBlockEntityBehavior>
    {
        private final Identifier id;
        private final Supplier<T> behavior;
        private List<SupportedRecipeType> recipeTypes;
        private RecipeSerializer serializer;
        private CoreBlockDef recipeCreatorBlock;
        private CoreBlockEntityDefinition blockEntityDefinition;
        private ContainerModel<T> containerModel;

        public Builder(Identifier id, Supplier<T> behavior)
        {
            this.id = id;
            this.behavior = behavior;
        }

        public Builder<T> setRecipeTypes(SupportedRecipeType... recipeTypes) {
            this.recipeTypes = List.of(recipeTypes);
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
            return new RecipeCreator<>(id, recipeTypes, serializer, recipeCreatorBlock, behavior, blockEntityDefinition, containerModel);
        }
    }
}
