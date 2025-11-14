package fr.en0ri4n.craftcreator.api.recipe.utils;

import fr.en0ri4n.craftcreator.impl.block.CCBlock;
import fr.en0ri4n.craftcreator.impl.fluid.CCFluid;
import fr.en0ri4n.craftcreator.utils.HasRegistryName;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeEntry
{
    private final boolean isTag;
    private final Identifier registryName;
    private final int count;

    public boolean hasLuck()
    {
        return false;
    }

    public boolean isBlock()
    {
        return false;
    }

    public static abstract class InputBase<T extends HasRegistryName> extends RecipeEntry
    {
        public InputBase(boolean isTag, Identifier registryName, int count)
        {
            super(isTag, registryName, count);
        }

        public abstract T getItem();
    }

    public static abstract class FluidInputBase<Fluid extends CCFluid> extends InputBase<Fluid>
    {
        public FluidInputBase(Fluid fluid, int amount)
        {
            super(false, fluid.getRegistryName(), amount);
        }

        public abstract Fluid getFluid();

        public int getAmount()
        {
            return getCount();
        }
    }

    public static abstract class BlockInputBase<Block extends CCBlock> extends InputBase<Block>
    {
        public BlockInputBase(Identifier registryName)
        {
            super(false, registryName, 1);
        }

        @Override
        public boolean isBlock()
        {
            return true;
        }

        public abstract Block getBlock();
    }

    public static abstract class Output<T extends HasRegistryName> extends RecipeEntry
    {
        public Output(Identifier registryName, int count)
        {
            super(false, registryName, count);
        }

        public abstract T getItem();
    }

    public static abstract class FluidOutputBase<Fluid extends CCFluid> extends Output<Fluid>
    {
        public FluidOutputBase(Fluid fluid, int amount)
        {
            super(fluid.getRegistryName(), amount);
        }

        public abstract Fluid getFluid();

        public int getAmount()
        {
            return getCount();
        }
    }

    public static abstract class BlockOutput<Block extends CCBlock> extends Output<Block>
    {
        public BlockOutput(Identifier registryName)
        {
            super(registryName, 1);
        }

        public abstract Block getBlock();
    }

    public static abstract class LuckedOutputBase<Item extends HasRegistryName> extends Output<Item>
    {
        @Getter
        private final double chance;

        public LuckedOutputBase(Identifier registryName, int count, double chance)
        {
            super(registryName, count);
            this.chance = chance;
        }

        @Override
        public boolean hasLuck()
        {
            return true;
        }
    }

    @Getter
    public static class MultiEntry<Recipe extends RecipeEntry>
    {
        private final List<Recipe> entries;

        public MultiEntry()
        {
            this.entries = new ArrayList<>();
        }

        public MultiEntry<Recipe> add(Recipe entry)
        {
            this.entries.add(entry);
            return this;
        }

        public Recipe get(int index)
        {
            return entries.get(index);
        }

        public int size()
        {
            return entries.size();
        }

        public boolean isEmpty()
        {
            return size() <= 0;
        }

    }

    public static class MultiInput<Item extends HasRegistryName> extends MultiEntry<InputBase<Item>>
    {
        public List<InputBase<Item>> getInputs()
        {
            return this.getEntries();
        }
    }

    public static class MultiOutput<Item extends HasRegistryName> extends MultiEntry<Output<Item>>
    {
        public List<Output<Item>> getOutputs()
        {
            return getEntries();
        }

        public Output<Item> getOneOutput()
        {
            return this.getOutputs().stream().findFirst().orElse(null);
        }
    }
}