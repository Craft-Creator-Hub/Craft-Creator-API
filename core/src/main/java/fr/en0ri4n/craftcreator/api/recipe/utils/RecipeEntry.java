package fr.en0ri4n.craftcreator.api.recipe.utils;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure data representation of a single recipe entry (item/block/fluid/tag).
 */
@Getter
@AllArgsConstructor
public class RecipeEntry {

    /** true if this is a tag reference ("forge:ingots/iron"), false for a concrete registry entry. */
    private final boolean tag;

    /** Registry name or tag id, e.g. "minecraft:iron_ingot" or "forge:ingots/iron". */
    private final Identifier id;

    /** Amount / count. For fluids, you interpret this as amount in mB, etc. */
    private final int count;

    /** 0.0–1.0; mostly used on outputs. 1.0 = guaranteed. */
    private final double chance;

    /** True if the referenced thing is a block (used e.g. in some Botania/Thermal datapacks). */
    private final boolean block;

    /** True if the referenced thing is a fluid. */
    private final boolean fluid;

    /* -------------------------------------------------------------------------
     * Convenience queries
     * ---------------------------------------------------------------------- */

    public boolean hasLuck() {
        return chance != 1.0D;
    }

    /* -------------------------------------------------------------------------
     * Static factories (for readability)
     * ---------------------------------------------------------------------- */

    // Items

    public static RecipeEntry item(Identifier id, int count) {
        return new RecipeEntry(false, id, count, 1.0D, false, false);
    }

    public static RecipeEntry item(Identifier id, int count, double chance) {
        return new RecipeEntry(false, id, count, chance, false, false);
    }

    public static RecipeEntry itemTag(Identifier tagId, int count) {
        return new RecipeEntry(true, tagId, count, 1.0D, false, false);
    }

    public static RecipeEntry itemTag(Identifier tagId, int count, double chance) {
        return new RecipeEntry(true, tagId, count, chance, false, false);
    }

    // Blocks

    public static RecipeEntry block(Identifier id) {
        return new RecipeEntry(false, id, 1, 1.0D, true, false);
    }

    public static RecipeEntry block(Identifier id, int count) {
        return new RecipeEntry(false, id, count, 1.0D, true, false);
    }

    // Fluids

    public static RecipeEntry fluid(Identifier id, int amount) {
        return new RecipeEntry(false, id, amount, 1.0D, false, true);
    }

    public static RecipeEntry fluid(Identifier id, int amount, double chance) {
        return new RecipeEntry(false, id, amount, chance, false, true);
    }

    /* -------------------------------------------------------------------------
     * Multi-input / multi-output containers
     * ---------------------------------------------------------------------- */

    @Getter
    public static class MultiInput {
        private final List<RecipeEntry> entries = new ArrayList<>();

        public MultiInput add(RecipeEntry entry) {
            if (entry != null) entries.add(entry);
            return this;
        }

        public MultiInput addAll(List<RecipeEntry> entries) {
            if (entries != null) this.entries.addAll(entries);
            return this;
        }

        public RecipeEntry get(int index) {
            return entries.get(index);
        }

        public int size() {
            return entries.size();
        }

        public boolean isEmpty() {
            return entries.isEmpty();
        }

        public List<RecipeEntry> asList() {
            return Collections.unmodifiableList(entries);
        }
    }

    @Getter
    public static class MultiOutput {
        private final List<RecipeEntry> entries = new ArrayList<>();

        public MultiOutput add(RecipeEntry entry) {
            if (entry != null) entries.add(entry);
            return this;
        }

        public MultiOutput addAll(List<RecipeEntry> entries) {
            if (entries != null) this.entries.addAll(entries);
            return this;
        }

        public RecipeEntry get(int index) {
            return entries.get(index);
        }

        public int size() {
            return entries.size();
        }

        public boolean isEmpty() {
            return entries.isEmpty();
        }

        public List<RecipeEntry> asList() {
            return Collections.unmodifiableList(entries);
        }

        /** Convenience for serializers that only support a single output. */
        public RecipeEntry getOneOutput() {
            return entries.isEmpty() ? null : entries.get(0);
        }
    }
}