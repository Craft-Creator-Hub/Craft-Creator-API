package fr.en0ri4n.craftcreator.recipe.slot;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.mod.SupportedMod;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Declarative description of a recipe type made from slots and typed values.<br>
 * Gives everything needed to build its container, screen and json: see {@link SlotRecipeTypes} for examples.
 */
@Getter
public class SlotRecipeType
{
    /** Unique id of this layout, also used as container model id. */
    private final Identifier id;
    /** Recipe type written in the json, e.g. "botania:mana_infusion". */
    private final Identifier recipeTypeId;
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
    private final Writer writer;

    private SlotRecipeType(Builder builder)
    {
        this.id = builder.id;
        this.recipeTypeId = builder.recipeTypeId;
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
        this.writer = Objects.requireNonNull(builder.writer, "writer");
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

    public static class Builder
    {
        private final Identifier id;
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
        private Writer writer;

        private Builder(SupportedMod mod, String name)
        {
            this.mod = mod;
            this.id = Identifier.fromMod(mod.getModId() + "_" + name);
            this.recipeTypeId = Identifier.from(mod.getModId(), name);
            this.title = "screen." + mod.getModId() + "_recipe_creator." + name + ".title";
            this.texture = Identifier.fromMod("textures/gui/container/" + mod.getModId() + "/" + name + "_recipe_creator.png");
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

        public SlotRecipeType writer(Writer writer)
        {
            this.writer = writer;
            return new SlotRecipeType(this);
        }
    }
}
