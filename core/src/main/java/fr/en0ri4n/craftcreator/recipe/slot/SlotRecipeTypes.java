package fr.en0ri4n.craftcreator.recipe.slot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.mod.SupportedMod;
import fr.en0ri4n.craftcreator.recipe.model.CraftingGrid;
import fr.en0ri4n.craftcreator.recipe.serialize.RecipeSerializer;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Every recipe type made from slots and values. Each one gets its own recipe creator block.<br>
 * Slot positions match the GUI textures in "textures/gui/container/{mod}/".
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SlotRecipeTypes
{
    private static final String FIELD = "screen.slot_recipe.field.";

    /* ---------------- Minecraft ---------------- */

    private static final int GRID_SIZE = 3;

    /** Crafting table: shaped (the grid layout is kept) or shapeless recipes. */
    public static final SlotRecipeType CRAFTING_TABLE = SlotRecipeType.builder(SupportedMod.MINECRAFT, "crafting")
            .id("crafting_table_recipe_creator")
            .icon("minecraft:crafting_table")
            .inputs("input", 30, 17, 48, 17, 66, 17, 30, 35, 48, 35, 66, 35, 30, 53, 48, 53, 66, 53)
            .slot(SlotSpec.output("output", 124, 35))
            .variant("shaped", "minecraft:crafting_shaped", "Shaped", "minecraft:crafting_table")
            .variant("shapeless", "minecraft:crafting_shapeless", "Shapeless", "minecraft:crafting_table")
            .variantSelector(100, 60, 50, 19, false)
            .inputReader(SlotRecipeTypes::readCraftingInputs)
            .writer((ctx, out) ->
            {
                List<RecipeEntry> inputs = ctx.all("input");
                if(ctx.getVariant().name().equals("shaped"))
                {
                    CraftingGrid grid = new CraftingGrid(GRID_SIZE, GRID_SIZE);
                    inputs.forEach(input -> grid.set(input.getSlot() % GRID_SIZE, input.getSlot() / GRID_SIZE, input));
                    RecipeSerializer.PatternAndKey patternAndKey = RecipeSerializer.buildPatternAndKey(grid);
                    out.add("pattern", patternAndKey.pattern());
                    out.add("key", patternAndKey.key());
                }
                else
                {
                    out.add("ingredients", ctx.ingredients(inputs));
                }
                out.add("result", ctx.result(ctx.get("output")));
            });

    /**
     * Inputs of a crafting recipe with their grid slot: from the pattern for shaped recipes, in order for shapeless ones.
     */
    private static List<RecipeEntry> readCraftingInputs(JsonObject json)
    {
        List<RecipeEntry> inputs = new ArrayList<>();
        if(json.has("pattern"))
        {
            JsonArray pattern = json.getAsJsonArray("pattern");
            JsonObject key = json.has("key") ? json.getAsJsonObject("key") : new JsonObject();
            for(int row = 0; row < Math.min(pattern.size(), GRID_SIZE); row++)
            {
                String line = pattern.get(row).getAsString();
                for(int col = 0; col < Math.min(line.length(), GRID_SIZE); col++)
                    addInput(inputs, key.get(String.valueOf(line.charAt(col))), row * GRID_SIZE + col);
            }
        }
        else if(json.has("ingredients"))
        {
            JsonArray ingredients = json.getAsJsonArray("ingredients");
            for(int i = 0; i < Math.min(ingredients.size(), GRID_SIZE * GRID_SIZE); i++)
                addInput(inputs, ingredients.get(i), i);
        }
        return inputs;
    }

    private static void addInput(List<RecipeEntry> inputs, JsonElement ingredient, int slot)
    {
        RecipeEntry entry = ingredient == null ? null : RecipeSerializer.parseIngredient(ingredient);
        if(entry == null) return;
        entry.setSlot(slot);
        inputs.add(entry);
    }

    /** Furnace, blast furnace, smoker and campfire share the same layout and values. */
    public static final SlotRecipeType FURNACE = SlotRecipeType.builder(SupportedMod.MINECRAFT, "smelting")
            .id("furnace_recipe_creator")
            .texture("textures/gui/container/minecraft/furnace_recipe_creator.png")
            .icon("minecraft:furnace")
            .slot(SlotSpec.input("input", 56, 35))
            .slot(SlotSpec.output("output", 116, 35))
            .variant("smelting", "minecraft:smelting", "Furnace", "minecraft:furnace")
            .variant("blasting", "minecraft:blasting", "Blast Furnace", "minecraft:blast_furnace")
            .variant("smoking", "minecraft:smoking", "Smoker", "minecraft:smoker")
            .variant("campfire_cooking", "minecraft:campfire_cooking", "Campfire", "minecraft:campfire")
            .variantSelector(176 - 22, 0, 22, 22, true)
            .param(ParamSpec.integer("cookingtime", "screen.minecraft_recipe_creator.smelting.field.cooking_time", 200))
            .param(ParamSpec.decimal("experience", "screen.minecraft_recipe_creator.smelting.field.experience", 0.1))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.ingredient(ctx.get("input")));
                out.addProperty("result", ctx.get("output").getId().toString());
                out.addProperty("experience", ctx.paramDouble("experience"));
                out.addProperty("cookingtime", ctx.paramInt("cookingtime"));
            });

    public static final SlotRecipeType STONECUTTING = SlotRecipeType.builder(SupportedMod.MINECRAFT, "stonecutting")
            .icon("minecraft:stonecutter")
            .slot(SlotSpec.input("input", 39, 33))
            .slot(SlotSpec.output("output", 114, 33))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.ingredient(ctx.get("input")));
                out.addProperty("result", ctx.get("output").getId().toString());
                out.addProperty("count", ctx.get("output").getCount());
            });

    public static final SlotRecipeType SMITHING = SlotRecipeType.builder(SupportedMod.MINECRAFT, "smithing")
            .icon("minecraft:smithing_table")
            .slot(SlotSpec.input("base", 27, 47))
            .slot(SlotSpec.input("addition", 76, 47))
            .slot(SlotSpec.output("output", 134, 47))
            .writer((ctx, out) ->
            {
                out.add("base", ctx.ingredient(ctx.get("base")));
                out.add("addition", ctx.ingredient(ctx.get("addition")));
                out.add("result", ctx.item(ctx.get("output")));
            });

    /* ---------------- Botania ---------------- */

    public static final SlotRecipeType MANA_INFUSION = SlotRecipeType.builder(SupportedMod.BOTANIA, "mana_infusion")
            .icon("botania:mana_pool")
            .slot(SlotSpec.input("input", 44, 37))
            .slot(SlotSpec.input("catalyst", 80, 61).as(SlotSpec.Kind.BLOCK))
            .slot(SlotSpec.output("output", 116, 37))
            .param(ParamSpec.integer("mana", FIELD + "mana", 1000))
            .writer((ctx, out) ->
            {
                out.add("input", ctx.ingredient(ctx.get("input")));
                out.add("output", ctx.item(ctx.get("output")));
                out.addProperty("mana", ctx.paramInt("mana"));
                if(ctx.has("catalyst")) out.add("catalyst", ctx.blockMatcher(ctx.get("catalyst")));
            });

    public static final SlotRecipeType ELVEN_TRADE = SlotRecipeType.builder(SupportedMod.BOTANIA, "elven_trade")
            .icon("botania:alfheim_portal")
            .inputs("input", 26, 19, 8, 37, 26, 37, 44, 37, 26, 55)
            .outputs("output", 134, 19, 116, 37, 134, 37, 152, 37, 134, 55)
            .writer((ctx, out) ->
            {
                out.add("ingredients", ctx.ingredients(ctx.all("input")));
                out.add("output", ctx.items(ctx.all("output")));
            });

    public static final SlotRecipeType PURE_DAISY = SlotRecipeType.builder(SupportedMod.BOTANIA, "pure_daisy")
            .icon("botania:pure_daisy")
            .slot(SlotSpec.input("input", 26, 53).as(SlotSpec.Kind.BLOCK))
            .slot(SlotSpec.output("output", 134, 53).as(SlotSpec.Kind.BLOCK))
            .param(ParamSpec.integer("time", FIELD + "time", 150))
            .writer((ctx, out) ->
            {
                out.add("input", ctx.blockMatcher(ctx.get("input")));
                JsonObject output = new JsonObject();
                output.addProperty("name", ctx.get("output").getId().toString());
                out.add("output", output);
                out.addProperty("time", ctx.paramInt("time"));
            });

    /** The brew is not an item, so it's typed in the settings instead of placed in an output slot. */
    public static final SlotRecipeType BREWERY = SlotRecipeType.builder(SupportedMod.BOTANIA, "brewery")
            .recipeType("botania:brew")
            .icon("botania:brewery")
            .inputs("input", 24, 12, 45, 12, 12, 33, 57, 33, 24, 54, 45, 54)
            .param(ParamSpec.text("brew", FIELD + "brew", "botania:speed"))
            .writer((ctx, out) ->
            {
                out.addProperty("brew", ctx.param("brew"));
                out.add("ingredients", ctx.ingredients(ctx.all("input")));
            });

    public static final SlotRecipeType PETAL_APOTHECARY = SlotRecipeType.builder(SupportedMod.BOTANIA, "petal_apothecary")
            .icon("botania:apothecary_default")
            .inputs("input", 15, 12, 35, 12, 55, 12, 23, 33, 43, 33, 63, 33, 15, 54, 35, 54, 55, 54)
            .slot(SlotSpec.output("output", 134, 33))
            .writer((ctx, out) ->
            {
                out.add("output", ctx.item(ctx.get("output")));
                out.add("ingredients", ctx.ingredients(ctx.all("input")));
            });

    public static final SlotRecipeType RUNIC_ALTAR = SlotRecipeType.builder(SupportedMod.BOTANIA, "runic_altar")
            .title("screen.botania_recipe_creator.rune_altar.title")
            .texture("textures/gui/container/botania/rune_altar_recipe_creator.png")
            .icon("botania:runic_altar")
            .inputs("input", 8, 8, 26, 8, 44, 8, 8, 26, 26, 26, 44, 26, 62, 26, 8, 44, 26, 44, 44, 44, 62, 44, 8, 62, 26, 62, 44, 62)
            .slot(SlotSpec.output("output", 134, 33))
            .param(ParamSpec.integer("mana", FIELD + "mana", 5200))
            .writer((ctx, out) ->
            {
                out.add("output", ctx.item(ctx.get("output")));
                out.addProperty("mana", ctx.paramInt("mana"));
                out.add("ingredients", ctx.ingredients(ctx.all("input")));
            });

    public static final SlotRecipeType TERRA_PLATE = SlotRecipeType.builder(SupportedMod.BOTANIA, "terra_plate")
            .icon("botania:terra_plate")
            .inputs("input", 34, 11, 12, 33, 56, 33, 22, 57, 47, 57)
            .slot(SlotSpec.output("output", 134, 33))
            .param(ParamSpec.integer("mana", FIELD + "mana", 500000))
            .writer((ctx, out) ->
            {
                out.add("result", ctx.item(ctx.get("output")));
                out.addProperty("mana", ctx.paramInt("mana"));
                out.add("ingredients", ctx.ingredients(ctx.all("input")));
            });

    /* ---------------- Thermal ---------------- */
    // Energy 0 means "keep the machine's default energy"

    private static final int[] THERMAL_OUTPUTS = {189, 33, 189, 59, 189, 85, 189, 111};

    private static SlotRecipeType.Builder thermal(String name, String icon)
    {
        return SlotRecipeType.builder(SupportedMod.THERMAL, name)
                .icon(icon)
                .gui(384, 296, 256, 68, 174)
                .param(ParamSpec.integer("energy", FIELD + "energy", 0));
    }

    private static SlotRecipeType.Builder withChances(SlotRecipeType.Builder builder, int outputs)
    {
        for(int i = 0; i < outputs; i++)
            builder.param(ParamSpec.decimal("chance_" + i, FIELD + "chance_" + i, 1.0));
        return builder;
    }

    /**
     * Thermal item results, with "chance" when it's not 100%.
     */
    private static JsonArray thermalResults(SlotRecipeContext ctx, String prefix)
    {
        JsonArray results = new JsonArray();
        List<RecipeEntry> outputs = ctx.all(prefix);
        for(RecipeEntry output : outputs)
        {
            JsonObject result = ctx.item(output);
            String slotName = ctx.getType().getSlots().get(output.getSlot()).name();
            double chance = ctx.paramDouble("chance_" + slotName.substring(slotName.lastIndexOf('_') + 1));
            if(chance != 1.0) result.addProperty("chance", chance);
            results.add(result);
        }
        return results;
    }

    private static void addExperience(SlotRecipeContext ctx, JsonObject out)
    {
        double experience = ctx.paramDouble("experience");
        if(experience > 0) out.addProperty("experience", experience);
    }

    public static final SlotRecipeType TREE_EXTRACTOR = thermal("tree_extractor", "thermal:device_tree_extractor")
            .slot(SlotSpec.input("trunk", 22, 98).as(SlotSpec.Kind.BLOCK))
            .slot(SlotSpec.input("leaves", 83, 98).as(SlotSpec.Kind.BLOCK))
            .slot(SlotSpec.output("fluid", 189, 72).as(SlotSpec.Kind.FLUID))
            .param(ParamSpec.integer("amount", FIELD + "fluid_amount", 25))
            .writer((ctx, out) ->
            {
                out.addProperty("trunk", ctx.get("trunk").getId().toString());
                out.addProperty("leaves", ctx.get("leaves").getId().toString());
                out.add("result", ctx.fluid(ctx.get("fluid"), ctx.paramInt("amount")));
            });

    public static final SlotRecipeType PULVERIZER = withChances(thermal("pulverizer", "thermal:machine_pulverizer"), 4)
            .slot(SlotSpec.input("input", 63, 98))
            .outputs("output", THERMAL_OUTPUTS)
            .param(ParamSpec.decimal("experience", FIELD + "experience", 0))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.countedIngredient(ctx.get("input")));
                out.add("result", thermalResults(ctx, "output"));
                addExperience(ctx, out);
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType SAWMILL = withChances(thermal("sawmill", "thermal:machine_sawmill"), 4)
            .slot(SlotSpec.input("input", 63, 98))
            .outputs("output", THERMAL_OUTPUTS)
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.countedIngredient(ctx.get("input")));
                out.add("result", thermalResults(ctx, "output"));
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType SMELTER = withChances(thermal("smelter", "thermal:machine_smelter"), 4)
            .inputs("input", 14, 44, 14, 62, 14, 80, 14, 98, 44, 44, 44, 62, 44, 80, 44, 98, 74, 44, 74, 62, 74, 80, 74, 98)
            .outputs("output", THERMAL_OUTPUTS)
            .param(ParamSpec.decimal("experience", FIELD + "experience", 0))
            .writer((ctx, out) ->
            {
                out.add("ingredients", ctx.countedIngredients(ctx.all("input")));
                out.add("result", thermalResults(ctx, "output"));
                addExperience(ctx, out);
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType INSOLATOR = withChances(thermal("insolator", "thermal:machine_insolator"), 4)
            .slot(SlotSpec.input("input", 63, 98))
            .outputs("output", THERMAL_OUTPUTS)
            .param(ParamSpec.decimal("water_mod", FIELD + "water_mod", 1.0))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.countedIngredient(ctx.get("input")));
                out.add("result", thermalResults(ctx, "output"));
                double waterMod = ctx.paramDouble("water_mod");
                if(waterMod != 1.0) out.addProperty("water_mod", waterMod);
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType PRESS = withChances(thermal("press", "thermal:machine_press"), 2)
            .slot(SlotSpec.input("input", 22, 98))
            .slot(SlotSpec.input("die", 83, 98))
            .outputs("output", 189, 41, 189, 103)
            .writer((ctx, out) ->
            {
                JsonArray ingredients = new JsonArray();
                ingredients.add(ctx.countedIngredient(ctx.get("input")));
                if(ctx.has("die")) ingredients.add(ctx.ingredient(ctx.get("die")));
                out.add("ingredients", ingredients);
                out.add("result", thermalResults(ctx, "output"));
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType THERMAL_FURNACE = thermal("furnace", "thermal:machine_furnace")
            .slot(SlotSpec.input("input", 63, 98))
            .slot(SlotSpec.output("output_0", 189, 72))
            .param(ParamSpec.decimal("experience", FIELD + "experience", 0))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.countedIngredient(ctx.get("input")));
                out.add("result", ctx.items(ctx.all("output")));
                addExperience(ctx, out);
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType CENTRIFUGE = withChances(thermal("centrifuge", "thermal:machine_centrifuge"), 4)
            .slot(SlotSpec.input("input", 63, 98))
            .outputs("output", THERMAL_OUTPUTS)
            .slot(SlotSpec.output("fluid", 242, 84).as(SlotSpec.Kind.FLUID))
            .param(ParamSpec.integer("fluid_amount", FIELD + "fluid_amount", 100))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.countedIngredient(ctx.get("input")));
                JsonArray results = thermalResults(ctx, "output");
                if(ctx.has("fluid")) results.add(ctx.fluid(ctx.get("fluid"), ctx.paramInt("fluid_amount")));
                out.add("result", results);
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType CHILLER = thermal("chiller", "thermal:machine_chiller")
            .slot(SlotSpec.input("fluid", 29, 98).as(SlotSpec.Kind.FLUID))
            .slot(SlotSpec.input("cast", 83, 98))
            .slot(SlotSpec.output("output_0", 189, 72))
            .param(ParamSpec.integer("fluid_amount", FIELD + "fluid_amount", 1000))
            .writer((ctx, out) ->
            {
                JsonArray ingredients = new JsonArray();
                ingredients.add(ctx.fluid(ctx.get("fluid"), ctx.paramInt("fluid_amount")));
                if(ctx.has("cast")) ingredients.add(ctx.ingredient(ctx.get("cast")));
                out.add("ingredients", ingredients);
                out.add("result", ctx.items(ctx.all("output")));
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType CRUCIBLE = thermal("crucible", "thermal:machine_crucible")
            .slot(SlotSpec.input("input", 63, 98))
            .slot(SlotSpec.output("fluid", 189, 72).as(SlotSpec.Kind.FLUID))
            .param(ParamSpec.integer("fluid_amount", FIELD + "fluid_amount", 1000))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.countedIngredient(ctx.get("input")));
                JsonArray results = new JsonArray();
                results.add(ctx.fluid(ctx.get("fluid"), ctx.paramInt("fluid_amount")));
                out.add("result", results);
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType REFINERY = withChances(thermal("refinery", "thermal:machine_refinery"), 1)
            .slot(SlotSpec.input("fluid", 63, 98).as(SlotSpec.Kind.FLUID))
            .slot(SlotSpec.output("output_0", 189, 33))
            .slot(SlotSpec.output("fluid_output_0", 189, 72).as(SlotSpec.Kind.FLUID))
            .slot(SlotSpec.output("fluid_output_1", 189, 111).as(SlotSpec.Kind.FLUID))
            .param(ParamSpec.integer("fluid_amount", FIELD + "input_fluid_amount", 100))
            .param(ParamSpec.integer("output_amount_0", FIELD + "output_fluid_amount_0", 50))
            .param(ParamSpec.integer("output_amount_1", FIELD + "output_fluid_amount_1", 50))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.fluid(ctx.get("fluid"), ctx.paramInt("fluid_amount")));
                JsonArray results = thermalResults(ctx, "output");
                if(ctx.has("fluid_output_0")) results.add(ctx.fluid(ctx.get("fluid_output_0"), ctx.paramInt("output_amount_0")));
                if(ctx.has("fluid_output_1")) results.add(ctx.fluid(ctx.get("fluid_output_1"), ctx.paramInt("output_amount_1")));
                out.add("result", results);
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType BOTTLER = thermal("bottler", "thermal:machine_bottler")
            .slot(SlotSpec.input("fluid", 29, 98).as(SlotSpec.Kind.FLUID))
            .slot(SlotSpec.input("input", 83, 98))
            .slot(SlotSpec.output("output_0", 189, 72))
            .param(ParamSpec.integer("fluid_amount", FIELD + "fluid_amount", 1000))
            .writer((ctx, out) ->
            {
                JsonArray ingredients = new JsonArray();
                ingredients.add(ctx.countedIngredient(ctx.get("input")));
                ingredients.add(ctx.fluid(ctx.get("fluid"), ctx.paramInt("fluid_amount")));
                out.add("ingredients", ingredients);
                out.add("result", ctx.items(ctx.all("output")));
                ctx.addIfPositive(out, "energy", "energy");
            });

    public static final SlotRecipeType PYROLYZER = withChances(thermal("pyrolyzer", "thermal:machine_pyrolyzer"), 4)
            .slot(SlotSpec.input("input", 63, 98))
            .outputs("output", THERMAL_OUTPUTS)
            .slot(SlotSpec.output("fluid", 259, 86).as(SlotSpec.Kind.FLUID))
            .param(ParamSpec.integer("fluid_amount", FIELD + "fluid_amount", 100))
            .param(ParamSpec.decimal("experience", FIELD + "experience", 0))
            .writer((ctx, out) ->
            {
                out.add("ingredient", ctx.countedIngredient(ctx.get("input")));
                JsonArray results = thermalResults(ctx, "output");
                if(ctx.has("fluid")) results.add(ctx.fluid(ctx.get("fluid"), ctx.paramInt("fluid_amount")));
                out.add("result", results);
                addExperience(ctx, out);
                ctx.addIfPositive(out, "energy", "energy");
            });

    /* ---------------- Create ---------------- */

    private static SlotRecipeType create(String name, String icon, int defaultTime)
    {
        return withChances(SlotRecipeType.builder(SupportedMod.CREATE, name)
                        .icon(icon)
                        .gui(256, 256, 256, 48, 174)
                        .slot(SlotSpec.input("input", 55, 74))
                        .outputs("output", 168, 56, 190, 56, 168, 78, 190, 78)
                        .param(ParamSpec.integer("processing_time", FIELD + "processing_time", defaultTime)), 4)
                .writer((ctx, out) ->
                {
                    JsonArray ingredients = new JsonArray();
                    ingredients.add(ctx.ingredient(ctx.get("input")));
                    out.add("ingredients", ingredients);
                    out.add("results", thermalResults(ctx, "output"));
                    out.addProperty("processingTime", ctx.paramInt("processing_time"));
                });
    }

    public static final SlotRecipeType CRUSHING = create("crushing", "create:crushing_wheel", 250);
    public static final SlotRecipeType CUTTING = create("cutting", "create:mechanical_saw", 100);

    public static final List<SlotRecipeType> ALL = List.of(
            CRAFTING_TABLE, FURNACE, STONECUTTING, SMITHING,
            MANA_INFUSION, ELVEN_TRADE, PURE_DAISY, BREWERY, PETAL_APOTHECARY, RUNIC_ALTAR, TERRA_PLATE,
            TREE_EXTRACTOR, PULVERIZER, SAWMILL, SMELTER, INSOLATOR, PRESS, THERMAL_FURNACE, CENTRIFUGE, CHILLER, CRUCIBLE, REFINERY, BOTTLER, PYROLYZER,
            CRUSHING, CUTTING);
}
