package fr.en0ri4n.craftcreator.api.blockentity;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Definition describing a core block-entity type: id, inventory size, and
 * registered behavior ids (strings) that will be looked up by CoreBlockEntityManager.
 */
@Getter
public class CoreBlockEntityDefinition {

    private final Identifier id;
    private final int inventorySize;
    private final List<String> behaviors;

    public CoreBlockEntityDefinition(Identifier id, int inventorySize, List<String> behaviors) {
        this.id = id;
        this.inventorySize = Math.max(0, inventorySize);
        this.behaviors = behaviors == null ? Collections.emptyList() : List.copyOf(behaviors);
    }

    public static Builder builder(Identifier id) { return new Builder(id); }

    public static class Builder {
        private final Identifier id;
        private int inventorySize = 0;
        private final List<String> behaviors = new ArrayList<>();

        public Builder(Identifier id) { this.id = id; }

        public Builder inventorySize(int size) { this.inventorySize = size; return this; }

        public Builder addBehavior(String behaviorId) { this.behaviors.add(behaviorId); return this; }

        public CoreBlockEntityDefinition build() {
            return new CoreBlockEntityDefinition(id, inventorySize, behaviors);
        }
    }
}