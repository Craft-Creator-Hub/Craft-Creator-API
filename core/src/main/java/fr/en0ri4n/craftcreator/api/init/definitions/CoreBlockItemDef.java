package fr.en0ri4n.craftcreator.api.init.definitions;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.Objects;

/**
 * Combined definition for a block and its optional block-item.
 * <p>
 * This is a small convenience wrapper so callers can declare "a block + its item"
 * in one place. Platform adapters may choose to register both the block and the
 * item (with the block's id) or only the block if they prefer.
 */
@Getter
public class CoreBlockItemDef {

    private final CoreBlockDef block;
    private final CoreItemDef item; // nullable — may be null when no item should be created

    private CoreBlockItemDef(CoreBlockDef block, CoreItemDef item) {
        this.block = Objects.requireNonNull(block, "block definition must not be null");
        this.item = item;
    }

    /**
     * Create a combined definition from existing block and item definitions.
     * item may be null to indicate "no block item".
     */
    public static CoreBlockItemDef of(CoreBlockDef block, CoreItemDef item) {
        return new CoreBlockItemDef(block, item);
    }

    /**
     * Create a builder initialized with the given id.
     * By default the builder will create a block AND a block-item with the same id.
     */
    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    public boolean hasItem() {
        return item != null;
    }

    /**
     * Builder to conveniently construct a CoreBlockItemDef.
     * - block properties are applied to the block
     * - item properties are applied to the generated block item (if enabled)
     */
    public static class Builder {
        private final CoreBlockDef.Builder blockBuilder;
        private final CoreItemDef.Builder itemBuilder;
        private boolean includeItem = true;

        public Builder(Identifier id) {
            this.blockBuilder = CoreBlockDef.builder(id);
            this.itemBuilder = CoreItemDef.builder(id);
        }

        /**
         * Configure max stack size for the block item.
         */
        public Builder itemMaxStackSize(int maxStackSize) {
            itemBuilder.maxStackSize(maxStackSize);
            return this;
        }

        /**
         * Whether to create/register a block item. Default: true.
         */
        public Builder includeItem(boolean include) {
            this.includeItem = include;
            return this;
        }

        public CoreBlockItemDef build() {
            CoreBlockDef block = blockBuilder.build();
            CoreItemDef item = includeItem ? itemBuilder.build() : null;
            return new CoreBlockItemDef(block, item);
        }
    }
}