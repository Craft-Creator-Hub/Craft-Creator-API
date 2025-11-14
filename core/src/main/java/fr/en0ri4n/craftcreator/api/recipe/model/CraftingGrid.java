package fr.en0ri4n.craftcreator.api.recipe.model;

import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeEntry;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Loader-agnostic representation of a crafting grid.
 * Coordinates are 0-based (x: column, y: row).
 */
@Getter
public class CraftingGrid {

    private final int width;
    private final int height;
    private final Map<Position, RecipeEntry> slots = new HashMap<>();

    public CraftingGrid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public CraftingGrid set(int x, int y, RecipeEntry entry) {
        if (entry != null) {
            slots.put(new Position(x, y), entry);
        }
        return this;
    }

    public RecipeEntry get(int x, int y) {
        return slots.get(new Position(x, y));
    }

    @Getter
    public static final class Position {
        private final int x;
        private final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Position p)) return false;
            return x == p.x && y == p.y;
        }

        @Override public int hashCode() {
            return 31 * x + y;
        }
    }
}