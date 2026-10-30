package fr.en0ri4n.craftcreator.recipe.slot;

/**
 * One slot of a {@link SlotRecipeType}.
 *
 * @param name   name used by the json writer to find this slot (e.g. "input", "catalyst", "output_2")
 * @param output whether the slot holds a recipe output
 * @param kind   what the item in the slot stands for
 * @param x      x position in the GUI texture
 * @param y      y position in the GUI texture
 */
public record SlotSpec(String name, boolean output, Kind kind, int x, int y)
{
    public enum Kind
    {
        /** The item itself (or a tag if the slot is tagged). */
        ITEM,
        /** The block placed by the item (block items share their block's id). */
        BLOCK,
        /** The fluid contained in the item (e.g. a bucket). */
        FLUID
    }

    public static SlotSpec input(String name, int x, int y)
    {
        return new SlotSpec(name, false, Kind.ITEM, x, y);
    }

    public static SlotSpec output(String name, int x, int y)
    {
        return new SlotSpec(name, true, Kind.ITEM, x, y);
    }

    public SlotSpec as(Kind kind)
    {
        return new SlotSpec(name, output, kind, x, y);
    }
}
