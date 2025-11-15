package fr.en0ri4n.craftcreator.api.init.definitions;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.Objects;

/**
 * Loader-agnostic description of an item to register.
 */
@Getter
public final class CoreItemDef
{
    private final Identifier id;
    private final int maxStackSize;

    private CoreItemDef(Identifier id, int maxStackSize) {
        this.id = Objects.requireNonNull(id, "id");
        this.maxStackSize = maxStackSize;
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final Identifier id;
        private int maxStackSize = 10;

        public Builder(Identifier id) {
            this.id = id;
        }

        public Builder maxStackSize(int size) {
            this.maxStackSize = size;
            return this;
        }

        public CoreItemDef build() {
            return new CoreItemDef(id, maxStackSize);
        }
    }
}