package fr.en0ri4n.craftcreator.recipe.slot;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.mod.SupportedMod;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Declarative description of a recipe type made from slots and typed values.<br>
 * Gives everything needed to build its container, screen and json: see {@link SlotRecipeTypes} for examples.
 * <p>
 * A type can have several {@link Variant}s sharing its slots, e.g. a furnace making smelting, blasting, smoking or
 * campfire recipes: the player picks one with the type's {@link VariantSelector}.
 */
@Getter
public class SlotRecipeType
{
    /** Unique id of this layout, also used as block, block entity and container model id. */
    private final Identifier id;
    private final SupportedMod mod;
    private final String title;
    /** Item representing this recipe type. */
    private final Identifier icon;
    private final Identifier texture;
    private final int textureSize;
    private final int guiWidth;
    private final int guiHeight;
    private final int inventoryX;
    private final int inventoryY;
    private final List<SlotSpec> slots;
    private final List<ParamSpec> params;
    /** Recipe types this layout can make; the first one is the default. Never empty. */
    private final List<Variant> variants;
    /** Where the variant can be changed, null when there is only one variant. */
    private final VariantSelector variantSelector;
    private final Writer writer;
    /** Reads the inputs of a recipe json with their slots, null to use the generic reader. */
    private final Function<JsonObject, List<RecipeEntry>> inputReader;
    /** Where the screen draws the title, null when the texture already shows it. */
    private final int[] titlePosition;
    /** Arrows, items and labels drawn by the screen over the texture. */
    private final List<Decoration> decorations;

    private SlotRecipeType(Builder builder)
    {
        this.id = builder.id;
        this.mod = builder.mod;
        this.title = builder.title;
        this.icon = Objects.requireNonNull(builder.icon, "icon");
        this.texture = builder.texture;
        this.textureSize = builder.textureSize;
        this.guiWidth = builder.guiWidth;
        this.guiHeight = builder.guiHeight;
        this.inventoryX = builder.inventoryX;
        this.inventoryY = builder.inventoryY;
        this.slots = List.copyOf(builder.slots);
        this.params = List.copyOf(builder.params);
        this.variants = builder.variants.isEmpty()
                ? List.of(new Variant(builder.name, builder.recipeTypeId, builder.name, this.icon))
                : List.copyOf(builder.variants);
        this.variantSelector = this.variants.size() > 1 ? Objects.requireNonNull(builder.variantSelector, "variant selector") : null;
        this.writer = Objects.requireNonNull(builder.writer, "writer");
        this.inputReader = builder.inputReader;
        this.titlePosition = builder.titlePosition;
        this.decorations = List.copyOf(builder.decorations);
    }

    /**
     * Start a recipe type named "{mod}:{name}", using the texture "textures/gui/container/{mod}/{name}_recipe_creator.png"
     * and the title "screen.{mod}_recipe_creator.{name}.title" unless changed.
     */
    public static Builder builder(SupportedMod mod, String name)
    {
        return new Builder(mod, name);
    }

    /**
     * @return the recipe type of the default variant
     */
    public Identifier getRecipeTypeId()
    {
        return variants.get(0).recipeTypeId();
    }

    public List<Identifier> getRecipeTypeIds()
    {
        return variants.stream().map(Variant::recipeTypeId).toList();
    }

    /**
     * @return the variant with this name, or the default one if there is none (e.g. renamed since it was saved)
     */
    public Variant getVariant(String name)
    {
        return variants.stream().filter(variant -> variant.name().equals(name)).findFirst().orElse(variants.get(0));
    }

    /**
     * @return the inventory index of the slot with the given name, or -1
     */
    public int indexOf(String slotName)
    {
        for(int i = 0; i < slots.size(); i++)
            if(slots.get(i).name().equals(slotName)) return i;
        return -1;
    }

    public Optional<ParamSpec> getParam(String key)
    {
        return params.stream().filter(param -> param.key().equals(key)).findFirst();
    }

    /**
     * Writes the recipe-specific part of the json (everything but "type", "id" and "name").
     */
    @FunctionalInterface
    public interface Writer
    {
        void write(SlotRecipeContext ctx, JsonObject out);
    }

    /**
     * One recipe type a layout can make.
     *
     * @param name         saved in the block entity
     * @param recipeTypeId written in the recipe json
     * @param label        shown in the variant selector
     * @param icon         item shown in the variant selector
     */
    public record Variant(String name, Identifier recipeTypeId, String label, Identifier icon) {}

    /**
     * Button cycling through the variants, at a position relative to the GUI.
     *
     * @param showIcon show the variant's item instead of its label
     */
    public record VariantSelector(int x, int y, int width, int height, boolean showIcon) {}

    /**
     * Something the screen draws over the GUI texture, at a position relative to the GUI.
     */
    public sealed interface Decoration permits Arrow, Item, Label, ParamIcon {}

    /**
     * A progress arrow pointing right (or down) from (x, y).
     */
    public record Arrow(int x, int y, int length, boolean down) implements Decoration {}

    /**
     * An item (e.g. the machine doing the recipe) drawn at (x, y), with a tooltip naming it.
     */
    public record Item(Identifier itemId, int x, int y) implements Decoration {}

    /**
     * A text, translation key or plain text, drawn at (x, y).
     */
    public record Label(String text, int x, int y, int color) implements Decoration {}

    /**
     * An item showing the current value of a {@link ParamSpec.Type#CHOICE} param, e.g. the machine of an assembly step.
     */
    public record ParamIcon(String paramKey, int x, int y, Map<String, Identifier> icons) implements Decoration {}

    public static class Builder
    {
        private Identifier id;
        private final String name;
        private final SupportedMod mod;
        private Identifier recipeTypeId;
        private String title;
        private Identifier icon;
        private Identifier texture;
        private int textureSize = 256;
        private int guiWidth = 176;
        private int guiHeight = 166;
        private int inventoryX = 8;
        private int inventoryY = 84;
        private final List<SlotSpec> slots = new ArrayList<>();
        private final List<ParamSpec> params = new ArrayList<>();
        private final List<Variant> variants = new ArrayList<>();
        private VariantSelector variantSelector;
        private Writer writer;
        private Function<JsonObject, List<RecipeEntry>> inputReader;
        private int[] titlePosition;
        private final List<Decoration> decorations = new ArrayList<>();

        private Builder(SupportedMod mod, String name)
        {
            this.mod = mod;
            this.name = name;
            this.id = Identifier.fromMod(mod.getModId() + "_" + name);
            this.recipeTypeId = Identifier.from(mod.getModId(), name);
            this.title = "screen." + mod.getModId() + "_recipe_creator." + name + ".title";
            this.texture = Identifier.fromMod("textures/gui/container/" + mod.getModId() + "/" + name + "_recipe_creator.png");
        }

        /**
         * Use another id than "{mod}_{name}", e.g. to keep the id of an existing block.
         */
        public Builder id(String path)
        {
            this.id = Identifier.fromMod(path);
            return this;
        }

        public Builder recipeType(String recipeTypeId)
        {
            this.recipeTypeId = Identifier.from(recipeTypeId);
            return this;
        }

        public Builder title(String title)
        {
            this.title = title;
            return this;
        }

        public Builder icon(String itemId)
        {
            this.icon = Identifier.from(itemId);
            return this;
        }

        public Builder texture(String path)
        {
            this.texture = Identifier.fromMod(path);
            return this;
        }

        /**
         * @param textureSize width and height of the texture file
         * @param width       width of the GUI inside the texture
         * @param height      height of the GUI inside the texture
         * @param inventoryX  x of the first player inventory slot
         * @param inventoryY  y of the first player inventory slot
         */
        public Builder gui(int textureSize, int width, int height, int inventoryX, int inventoryY)
        {
            this.textureSize = textureSize;
            this.guiWidth = width;
            this.guiHeight = height;
            this.inventoryX = inventoryX;
            this.inventoryY = inventoryY;
            return this;
        }

        public Builder slot(SlotSpec slot)
        {
            this.slots.add(slot);
            return this;
        }

        /**
         * Add numbered input slots named {@code prefix_0}, {@code prefix_1}... at the given (x, y) positions.
         */
        public Builder inputs(String prefix, int... positions)
        {
            for(int i = 0; i < positions.length / 2; i++)
                slot(SlotSpec.input(prefix + "_" + i, positions[i * 2], positions[i * 2 + 1]));
            return this;
        }

        /**
         * Add numbered output slots named {@code prefix_0}, {@code prefix_1}... at the given (x, y) positions.
         */
        public Builder outputs(String prefix, int... positions)
        {
            for(int i = 0; i < positions.length / 2; i++)
                slot(SlotSpec.output(prefix + "_" + i, positions[i * 2], positions[i * 2 + 1]));
            return this;
        }

        public Builder param(ParamSpec param)
        {
            this.params.add(param);
            return this;
        }

        /**
         * Add a recipe type this layout can make. The first one added is the default.
         */
        public Builder variant(String name, String recipeTypeId, String label, String icon)
        {
            this.variants.add(new Variant(name, Identifier.from(recipeTypeId), label, Identifier.from(icon)));
            return this;
        }

        /**
         * Where the button changing the variant is, relative to the GUI. Required with several variants.
         */
        public Builder variantSelector(int x, int y, int width, int height, boolean showIcon)
        {
            this.variantSelector = new VariantSelector(x, y, width, height, showIcon);
            return this;
        }

        /**
         * Read the inputs of a recipe json (with their slot index) for formats the generic reader can't place,
         * e.g. shaped crafting patterns.
         */
        public Builder inputReader(Function<JsonObject, List<RecipeEntry>> inputReader)
        {
            this.inputReader = inputReader;
            return this;
        }

        /**
         * Let the screen draw the title at (x, y), for textures that don't show it.
         */
        public Builder titleAt(int x, int y)
        {
            this.titlePosition = new int[] {x, y};
            return this;
        }

        public Builder arrow(int x, int y, int length)
        {
            this.decorations.add(new Arrow(x, y, length, false));
            return this;
        }

        public Builder arrowDown(int x, int y, int length)
        {
            this.decorations.add(new Arrow(x, y, length, true));
            return this;
        }

        public Builder item(String itemId, int x, int y)
        {
            this.decorations.add(new Item(Identifier.from(itemId), x, y));
            return this;
        }

        public Builder label(String text, int x, int y, int color)
        {
            this.decorations.add(new Label(text, x, y, color));
            return this;
        }

        /**
         * @param icons item shown for each value of the param; values without an item show nothing
         */
        public Builder paramIcon(String paramKey, int x, int y, Map<String, String> icons)
        {
            Map<String, Identifier> ids = new java.util.LinkedHashMap<>();
            icons.forEach((value, item) -> ids.put(value, Identifier.from(item)));
            this.decorations.add(new ParamIcon(paramKey, x, y, ids));
            return this;
        }

        public SlotRecipeType writer(Writer writer)
        {
            this.writer = writer;
            return new SlotRecipeType(this);
        }
    }
}
