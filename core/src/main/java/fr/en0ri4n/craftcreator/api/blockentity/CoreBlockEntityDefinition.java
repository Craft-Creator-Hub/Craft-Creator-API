package fr.en0ri4n.craftcreator.api.blockentity;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

/**
 * Definition describing a core block-entity type: id, inventory size, and
 * registered behavior ids (strings) that will be looked up by CoreBlockEntityManager.
 */
@Getter
public class CoreBlockEntityDefinition {

    private final Identifier id;
    private final int inventorySize;
    private final Identifier behavior;

    public CoreBlockEntityDefinition(Identifier id, int inventorySize, Identifier behavior) {
        this.id = id;
        this.inventorySize = Math.max(0, inventorySize);
        this.behavior = behavior == null ? Identifier.EMPTY : behavior;
    }

    public static Builder builder(Identifier id) { return new Builder(id); }

    public static class Builder {
        private final Identifier id;
        private int inventorySize = 0;
        private Identifier behavior;

        public Builder(Identifier id) { this.id = id; }

        public Builder inventorySize(int size) { this.inventorySize = size; return this; }

        public Builder setBehavior(Identifier behaviorId) { this.behavior = behaviorId; return this; }

        public CoreBlockEntityDefinition build() { return new CoreBlockEntityDefinition(id, inventorySize, behavior); }
    }
}