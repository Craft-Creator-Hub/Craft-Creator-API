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
import java.util.Map;

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
    // The GUI textures hold the panel and the slots; arrows and machines are decorations drawn by the screen

    private static final String CREATE_FIELD = "screen.create_recipe_creator.field.";
    private static final int CREATE_OUTPUTS_X = 112;
    private static final int[] CREATE_OUTPUTS = {CREATE_OUTPUTS_X, 29, CREATE_OUTPUTS_X + 18, 29, CREATE_OUTPUTS_X, 47, CREATE_OUTPUTS_X + 18, 47};

    private static SlotRecipeType.Builder create(String name, String icon)
    {
        return SlotRecipeType.builder(SupportedMod.CREATE, name)
                .icon(icon)
                .gui(256, 176, 166, 8, 84)
                .titleAt(8, 6);
    }

    /**
     * One input, an arrow with the machines above it and up to 4 outputs with chances: crushing, milling, washing...
     */
    private static SlotRecipeType.Builder createProcessing(String name, String icon, String... machines)
    {
        SlotRecipeType.Builder builder = withChances(create(name, icon), 4)
                .slot(SlotSpec.input("input", 30, 38))
                .arrow(52, 42, 48)
                .outputs("output", CREATE_OUTPUTS);
        int x = 76 - machines.length * 9;
        for(String machine : machines)
        {
            builder.item(machine, x, 21);
            x += 18;
        }
        return builder;
    }

    /**
     * Create ingredients have no count: a stack of n items is written n times, like Create's own recipes.
     */
    private static JsonArray createIngredients(SlotRecipeContext ctx, List<RecipeEntry> entries)
    {
        JsonArray array = new JsonArray();
        for(RecipeEntry entry : entries)
            for(int i = 0; i < Math.max(1, entry.getCount()); i++)
                array.add(ctx.ingredient(entry));
        return array;
    }

    private static JsonArray createIngredient(SlotRecipeContext ctx, String slot)
    {
        JsonArray array = new JsonArray();
        if(ctx.has(slot)) array.add(ctx.ingredient(ctx.get(slot)));
        return array;
    }

    /** Ingredients, results with chances and the processing time when the type has one (0 = Create's default). */
    private static void writeProcessing(SlotRecipeContext ctx, JsonObject out)
    {
        out.add("ingredients", createIngredient(ctx, "input"));
        out.add("results", thermalResults(ctx, "output"));
        if(ctx.getType().getParam("processing_time").isPresent())
            ctx.addIfPositive(out, "processingTime", "processing_time");
    }

    private static ParamSpec processingTime(int defaultTime)
    {
        return ParamSpec.integer("processing_time", FIELD + "processing_time", defaultTime);
    }

    public static final SlotRecipeType CRUSHING = createProcessing("crushing", "create:crushing_wheel", "create:crushing_wheel")
            .param(processingTime(250))
            .writer(SlotRecipeTypes::writeProcessing);

    public static final SlotRecipeType MILLING = createProcessing("milling", "create:millstone", "create:millstone")
            .param(processingTime(100))
            .writer(SlotRecipeTypes::writeProcessing);

    public static final SlotRecipeType CUTTING = createProcessing("cutting", "create:mechanical_saw", "create:mechanical_saw")
            .param(processingTime(100))
            .writer(SlotRecipeTypes::writeProcessing);

    public static final SlotRecipeType PRESSING = createProcessing("pressing", "create:mechanical_press", "create:mechanical_press", "create:depot")
            .writer(SlotRecipeTypes::writeProcessing);

    /** Bulk washing: an encased fan blowing through water. */
    public static final SlotRecipeType SPLASHING = createProcessing("splashing", "create:encased_fan", "create:encased_fan", "minecraft:water_bucket")
            .writer(SlotRecipeTypes::writeProcessing);

    /** Bulk haunting: an encased fan blowing through soul fire. */
    public static final SlotRecipeType HAUNTING = createProcessing("haunting", "create:encased_fan", "create:encased_fan", "minecraft:soul_campfire")
            .writer(SlotRecipeTypes::writeProcessing);

    public static final SlotRecipeType SANDPAPER_POLISHING = createProcessing("sandpaper_polishing", "create:sand_paper", "create:sand_paper")
            .writer(SlotRecipeTypes::writeProcessing);

    /**
     * The held item is applied to the input: by a deployer, or by a player for item application.
     */
    private static SlotRecipeType createApplying(String name, String icon, String machine)
    {
        return withChances(create(name, icon), 4)
                .slot(SlotSpec.input("held", 30, 29))
                .slot(SlotSpec.input("input", 30, 47))
                .arrow(52, 42, 48)
                .item(machine, 68, 21)
                .outputs("output", CREATE_OUTPUTS)
                .param(ParamSpec.bool("keep_held_item", CREATE_FIELD + "keep_held_item", false))
                .writer((ctx, out) ->
                {
                    JsonArray ingredients = createIngredient(ctx, "input");
                    ingredients.addAll(createIngredient(ctx, "held"));
                    out.add("ingredients", ingredients);
                    out.add("results", thermalResults(ctx, "output"));
                    if(ctx.paramBool("keep_held_item")) out.addProperty("keepHeldItem", true);
                });
    }

    public static final SlotRecipeType DEPLOYING = createApplying("deploying", "create:deployer", "create:deployer");
    public static final SlotRecipeType ITEM_APPLICATION = createApplying("item_application", "create:andesite_casing", "create:andesite_casing");

    /** A spout pours a fluid on the input. */
    public static final SlotRecipeType FILLING = create("filling", "create:spout")
            .slot(SlotSpec.input("fluid", 30, 29).as(SlotSpec.Kind.FLUID))
            .slot(SlotSpec.input("input", 30, 47))
            .arrow(52, 42, 48)
            .item("create:spout", 68, 21)
            .slot(SlotSpec.output("output_0", 121, 38))
            .param(ParamSpec.integer("fluid_amount", FIELD + "fluid_amount", 250))
            .writer((ctx, out) ->
            {
                JsonArray ingredients = createIngredient(ctx, "input");
                if(ctx.has("fluid")) ingredients.add(ctx.fluid(ctx.get("fluid"), ctx.paramInt("fluid_amount")));
                out.add("ingredients", ingredients);
                out.add("results", ctx.items(ctx.all("output")));
            });

    /** An item drain empties the input into an item and a fluid. */
    public static final SlotRecipeType EMPTYING = create("emptying", "create:item_drain")
            .slot(SlotSpec.input("input", 30, 38))
            .arrow(52, 42, 48)
            .item("create:item_drain", 68, 21)
            .slot(SlotSpec.output("output_0", 121, 29))
            .slot(SlotSpec.output("fluid_output", 121, 47).as(SlotSpec.Kind.FLUID))
            .param(ParamSpec.integer("fluid_amount", FIELD + "fluid_amount", 250))
            .writer((ctx, out) ->
            {
                out.add("ingredients", createIngredient(ctx, "input"));
                JsonArray results = ctx.items(ctx.all("output"));
                if(ctx.has("fluid_output")) results.add(ctx.fluid(ctx.get("fluid_output"), ctx.paramInt("fluid_amount")));
                out.add("results", results);
            });

    /**
     * Recipes made in a basin: up to 9 items and 2 fluids in, 4 items and a fluid out, with a heat level.
     */
    private static SlotRecipeType createBasin(String name, String icon, String machine)
    {
        return withChances(create(name, icon), 4)
                .inputs("input", 8, 20, 26, 20, 44, 20, 8, 38, 26, 38, 44, 38, 8, 56, 26, 56, 44, 56)
                .slot(SlotSpec.input("fluid_in_0", 66, 29).as(SlotSpec.Kind.FLUID))
                .slot(SlotSpec.input("fluid_in_1", 66, 47).as(SlotSpec.Kind.FLUID))
                .arrow(87, 42, 21)
                .item(machine, 89, 21)
                .paramIcon("heat", 89, 57, Map.of("heated", "create:blaze_burner", "superheated", "create:blaze_cake"))
                .outputs("output", CREATE_OUTPUTS)
                .slot(SlotSpec.output("fluid_out_0", 152, 38).as(SlotSpec.Kind.FLUID))
                .param(ParamSpec.choice("heat", CREATE_FIELD + "heat", "none", "heated", "superheated"))
                .param(ParamSpec.integer("fluid_amount_0", CREATE_FIELD + "fluid_amount_0", 250))
                .param(ParamSpec.integer("fluid_amount_1", CREATE_FIELD + "fluid_amount_1", 250))
                .param(ParamSpec.integer("output_fluid_amount", CREATE_FIELD + "output_fluid_amount", 250))
                .param(ParamSpec.integer("processing_time", CREATE_FIELD + "processing_time", 0))
                .writer((ctx, out) ->
                {
                    JsonArray ingredients = createIngredients(ctx, ctx.all("input"));
                    for(int i = 0; i < 2; i++)
                        if(ctx.has("fluid_in_" + i))
                            ingredients.add(ctx.fluid(ctx.get("fluid_in_" + i), ctx.paramInt("fluid_amount_" + i)));
                    out.add("ingredients", ingredients);

                    JsonArray results = thermalResults(ctx, "output");
                    if(ctx.has("fluid_out_0")) results.add(ctx.fluid(ctx.get("fluid_out_0"), ctx.paramInt("output_fluid_amount")));
                    out.add("results", results);

                    if(!ctx.param("heat").equals("none")) out.addProperty("heatRequirement", ctx.param("heat"));
                    ctx.addIfPositive(out, "processingTime", "processing_time");
                });
    }

    public static final SlotRecipeType MIXING = createBasin("mixing", "create:mechanical_mixer", "create:mechanical_mixer");
    public static final SlotRecipeType COMPACTING = createBasin("compacting", "create:mechanical_press", "create:mechanical_press");

    /** Largest mechanical crafting grid, kept small enough for the GUI to fit the screen at GUI scale 4. */
    private static final int CRAFTER_SIZE = 7;

    /** Mechanical crafters: shaped recipes on a grid up to 7x7. */
    public static final SlotRecipeType MECHANICAL_CRAFTING = mechanicalCrafting();

    private static SlotRecipeType mechanicalCrafting()
    {
        int[] positions = new int[CRAFTER_SIZE * CRAFTER_SIZE * 2];
        for(int i = 0; i < CRAFTER_SIZE * CRAFTER_SIZE; i++)
        {
            positions[i * 2] = 8 + (i % CRAFTER_SIZE) * 18;
            positions[i * 2 + 1] = 20 + (i / CRAFTER_SIZE) * 18;
        }
        return SlotRecipeType.builder(SupportedMod.CREATE, "mechanical_crafting")
                .icon("create:mechanical_crafter")
                .gui(256, 194, 242, 17, 160)
                .titleAt(8, 6)
                .inputs("input", positions)
                .arrow(142, 78, 22)
                .item("create:mechanical_crafter", 145, 57)
                .slot(SlotSpec.output("output", 170, 74))
                .param(ParamSpec.bool("accept_mirrored", CREATE_FIELD + "accept_mirrored", true))
                .inputReader(json -> readPatternInputs(json, CRAFTER_SIZE))
                .writer((ctx, out) ->
                {
                    CraftingGrid grid = new CraftingGrid(CRAFTER_SIZE, CRAFTER_SIZE);
                    ctx.all("input").forEach(input -> grid.set(input.getSlot() % CRAFTER_SIZE, input.getSlot() / CRAFTER_SIZE, input));
                    RecipeSerializer.PatternAndKey patternAndKey = RecipeSerializer.buildPatternAndKey(grid);
                    out.add("pattern", patternAndKey.pattern());
                    out.add("key", patternAndKey.key());
                    out.add("result", ctx.result(ctx.get("output")));
                    out.addProperty("acceptMirrored", ctx.paramBool("accept_mirrored"));
                });
    }

    private static final int ASSEMBLY_STEPS = 5;
    private static final int ASSEMBLY_STEP_X = 46;
    private static final int ASSEMBLY_STEP_SPACING = 22;

    /**
     * Sequenced assembly: the input goes through up to 5 steps (deploying, filling, pressing or cutting) as the
     * transitional item, a number of times, then gives one of the outputs, picked by weight.
     */
    public static final SlotRecipeType SEQUENCED_ASSEMBLY = sequencedAssembly();

    private static SlotRecipeType sequencedAssembly()
    {
        SlotRecipeType.Builder builder = withWeights(SlotRecipeType.builder(SupportedMod.CREATE, "sequenced_assembly")
                .icon("create:precision_mechanism")
                .gui(256, 176, 194, 8, 112)
                .titleAt(8, 6)
                .slot(SlotSpec.input("input", 10, 34))
                .arrow(29, 38, 14)
                .slot(SlotSpec.input("transitional", 10, 72))
                .arrow(30, 76, 76)
                .outputs("output", 112, 63, 130, 63, 112, 81, 130, 81)
                .param(ParamSpec.integer("loops", CREATE_FIELD + "loops", 1)));

        Map<String, String> stepIcons = Map.of("deploying", "create:deployer", "filling", "create:spout",
                                                "pressing", "create:mechanical_press", "cutting", "create:mechanical_saw");
        for(int i = 0; i < ASSEMBLY_STEPS; i++)
        {
            int x = ASSEMBLY_STEP_X + i * ASSEMBLY_STEP_SPACING;
            builder.slot(SlotSpec.input("step_" + i, x, 34))
                    .paramIcon("step_type_" + i, x, 16, stepIcons)
                    .label(String.valueOf(i + 1), x + 5, 54, 0xFFE6D3A3);
            // The first step is set by default so a new recipe already does something
            builder.param(i == 0
                                  ? ParamSpec.choice("step_type_" + i, CREATE_FIELD + "step_type_" + i, "deploying", "filling", "pressing", "cutting", "none")
                                  : ParamSpec.choice("step_type_" + i, CREATE_FIELD + "step_type_" + i, "none", "deploying", "filling", "pressing", "cutting"));
            builder.param(ParamSpec.integer("step_amount_" + i, CREATE_FIELD + "step_amount_" + i, 250));
        }

        return builder.writer((ctx, out) ->
        {
            if(ctx.has("input")) out.add("ingredient", ctx.ingredient(ctx.get("input")));
            RecipeEntry transitional = ctx.has("transitional") ? ctx.get("transitional") : ctx.get("output_0");
            JsonObject transitionalItem = new JsonObject();
            if(transitional != null) transitionalItem.addProperty("item", transitional.getId().toString());
            out.add("transitionalItem", transitionalItem);

            JsonArray sequence = new JsonArray();
            for(int i = 0; i < ASSEMBLY_STEPS; i++)
            {
                String stepType = ctx.param("step_type_" + i);
                if(!stepType.equals("none"))
                    sequence.add(assemblyStep(ctx, i, stepType, transitionalItem));
            }
            out.add("sequence", sequence);

            // Create uses the chances of sequenced assembly results as weights
            out.add("results", thermalResults(ctx, "output"));
            out.addProperty("loops", Math.max(1, ctx.paramInt("loops")));
        });
    }

    private static JsonObject assemblyStep(SlotRecipeContext ctx, int index, String stepType, JsonObject transitionalItem)
    {
        JsonObject step = new JsonObject();
        step.addProperty("type", "create:" + stepType);

        JsonArray ingredients = new JsonArray();
        ingredients.add(transitionalItem.deepCopy());
        String slot = "step_" + index;
        if(stepType.equals("deploying") && ctx.has(slot))
            ingredients.add(ctx.ingredient(ctx.get(slot)));
        if(stepType.equals("filling"))
        {
            RecipeEntry fluid = ctx.fluidOf(slot);
            if(fluid != null) ingredients.add(ctx.fluid(fluid, ctx.paramInt("step_amount_" + index)));
        }
        step.add("ingredients", ingredients);

        JsonArray results = new JsonArray();
        results.add(transitionalItem.deepCopy());
        step.add("results", results);
        return step;
    }

    private static SlotRecipeType.Builder withWeights(SlotRecipeType.Builder builder)
    {
        for(int i = 0; i < 4; i++)
            builder.param(ParamSpec.decimal("chance_" + i, CREATE_FIELD + "weight_" + i, 1.0));
        return builder;
    }

    /**
     * Inputs of a shaped pattern with their slot, on a square grid of the given size.
     */
    private static List<RecipeEntry> readPatternInputs(JsonObject json, int size)
    {
        List<RecipeEntry> inputs = new ArrayList<>();
        if(!json.has("pattern")) return inputs;
        JsonArray pattern = json.getAsJsonArray("pattern");
        JsonObject key = json.has("key") ? json.getAsJsonObject("key") : new JsonObject();
        for(int row = 0; row < Math.min(pattern.size(), size); row++)
        {
            String line = pattern.get(row).getAsString();
            for(int col = 0; col < Math.min(line.length(), size); col++)
                addInput(inputs, key.get(String.valueOf(line.charAt(col))), row * size + col);
        }
        return inputs;
    }

    public static final List<SlotRecipeType> ALL = List.of(
            CRAFTING_TABLE, FURNACE, STONECUTTING, SMITHING,
            MANA_INFUSION, ELVEN_TRADE, PURE_DAISY, BREWERY, PETAL_APOTHECARY, RUNIC_ALTAR, TERRA_PLATE,
            TREE_EXTRACTOR, PULVERIZER, SAWMILL, SMELTER, INSOLATOR, PRESS, THERMAL_FURNACE, CENTRIFUGE, CHILLER, CRUCIBLE, REFINERY, BOTTLER, PYROLYZER,
            CRUSHING, MILLING, CUTTING, PRESSING, SPLASHING, HAUNTING, SANDPAPER_POLISHING, DEPLOYING, ITEM_APPLICATION,
            FILLING, EMPTYING, MIXING, COMPACTING, MECHANICAL_CRAFTING, SEQUENCED_ASSEMBLY);
}
