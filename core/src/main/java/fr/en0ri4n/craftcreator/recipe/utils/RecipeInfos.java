package fr.en0ri4n.craftcreator.recipe.utils;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.*;

/**
 * Loader-agnostic container for extra recipe parameters (time, energy, flags, etc.).
 * <p/>
 * JSON (de)serialization is handled by RecipeInfosSerializer via JsonSerializer/SerializerRegistry.
 */
public class RecipeInfos {

    @Getter
    private final List<RecipeParameter> parameters;

    private RecipeInfos() {
        this.parameters = new ArrayList<>();
    }

    public static RecipeInfos create() {
        return new RecipeInfos();
    }

    /* -------------------------------------------------------------------------
     * Parameter management
     * ---------------------------------------------------------------------- */

    public void addParameter(RecipeParameter parameter) {
        Objects.requireNonNull(parameter, "parameter");
        removeParameter(parameter.getName());
        this.parameters.add(parameter);
    }

    public void removeParameter(String name) {
        parameters.removeIf(p -> Objects.equals(p.getName(), name));
    }

    public RecipeParameter getRecipeParameter(String name) {
        return this.parameters.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(RecipeParameter.EMPTY);
    }

    public boolean contains(String name) {
        return getRecipeParameter(name) != RecipeParameter.EMPTY;
    }

    /* -------------------------------------------------------------------------
     * Type-safe getters
     * ---------------------------------------------------------------------- */

    public Number getNumber(String name, Number def) {
        RecipeParameter p = getRecipeParameter(name);
        return p instanceof RecipeParameterNumber ? ((RecipeParameterNumber) p).getNumberValue() : def;
    }

    public int getInt(String name, int def) {
        Number n = getNumber(name, def);
        return n == null ? def : n.intValue();
    }

    public double getDouble(String name, double def) {
        Number n = getNumber(name, def);
        return n == null ? def : n.doubleValue();
    }

    public boolean getBoolean(String name, boolean def) {
        RecipeParameter p = getRecipeParameter(name);
        return p instanceof RecipeParameterBoolean ? ((RecipeParameterBoolean) p).getBoolean() : def;
    }

    public Map<Integer, Identifier> getMap(String name) {
        RecipeParameter p = getRecipeParameter(name);
        return p instanceof RecipeParameterMap ? ((RecipeParameterMap) p).getMap() : Collections.emptyMap();
    }

    public List<Integer> getList(String name) {
        RecipeParameter p = getRecipeParameter(name);
        return p instanceof RecipeParameterIntList ? ((RecipeParameterIntList) p).getList() : Collections.emptyList();
    }

    /* -------------------------------------------------------------------------
     * Type-safe setters
     * ---------------------------------------------------------------------- */

    public RecipeInfos setInt(String name, int value) {
        addParameter(new RecipeParameterNumber(name, value, false));
        return this;
    }

    public RecipeInfos setDouble(String name, double value) {
        addParameter(new RecipeParameterNumber(name, value, true));
        return this;
    }

    public RecipeInfos setNumber(String name, Number value, boolean isDouble) {
        addParameter(new RecipeParameterNumber(name, value, isDouble));
        return this;
    }

    public RecipeInfos setBoolean(String name, boolean value) {
        addParameter(new RecipeParameterBoolean(name, value));
        return this;
    }

    public RecipeInfos setMap(String name, Map<Integer, Identifier> map) {
        addParameter(new RecipeParameterMap(name, new HashMap<>(map)));
        return this;
    }

    public RecipeInfos setList(String name, List<Integer> list) {
        addParameter(new RecipeParameterIntList(name, new ArrayList<>(list)));
        return this;
    }

    /* -------------------------------------------------------------------------
     * Parameter types
     * ---------------------------------------------------------------------- */

    @Getter
    public static class RecipeParameter {
        public static final RecipeParameter EMPTY = new RecipeParameter(RecipeParameterType.EMPTY, "empty");

        private final RecipeParameterType type;
        private final String name;

        public RecipeParameter(RecipeParameterType type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getRawValue() {
            return null;
        }
    }

    public static class RecipeParameterNumber extends RecipeParameter {
        private final Number value;
        @Getter
        private final boolean isDouble;

        public RecipeParameterNumber(String name, Number value, boolean isDouble) {
            super(RecipeParameterType.NUMBER, name);
            this.value = value;
            this.isDouble = isDouble;
        }

        public Number getNumberValue() {
            return value;
        }

        @Override
        public String getRawValue()
        {
            return value.toString();
        }
    }

    public static class RecipeParameterBoolean extends RecipeParameter {
        private final boolean value;

        public RecipeParameterBoolean(String name, boolean value) {
            super(RecipeParameterType.BOOLEAN, name);
            this.value = value;
        }

        public boolean getBoolean() {
            return value;
        }

        @Override
        public String getRawValue()
        {
            return Boolean.toString(value);
        }
    }

    @Getter
    public static class RecipeParameterMap extends RecipeParameter {
        private final Map<Integer, Identifier> map;

        public RecipeParameterMap(String name, Map<Integer, Identifier> map) {
            super(RecipeParameterType.MAP, name);
            this.map = map;
        }

        @Override
        public String getRawValue()
        {
            return "Map(" + map.size() + " entries)";
        }
    }

    @Getter
    public static class RecipeParameterIntList extends RecipeParameter {
        private final List<Integer> list;

        public RecipeParameterIntList(String name, List<Integer> list) {
            super(RecipeParameterType.INT_LIST, name);
            this.list = list;
        }

        @Override
        public String getRawValue()
        {
            return "IntList(" + list.size() + " elements)";
        }
    }

    public static class Parameters {
        // Base
        public static final String SHAPED = "shaped";
        public static final String TAGGED_SLOTS = "tagged_slots";
        public static final String KUBEJS_RECIPE = "kubejs_recipe";
        public static final String NBT_SLOTS = "nbt_slots";

        // Vanilla
        public static final String EXPERIENCE = "experience";
        public static final String COOKING_TIME = "cooking_time";

        // Botania
        public static final String TIME = "time";
        public static final String MANA = "mana";

        // Thermal
        public static final String ENERGY = "energy";
        public static final String ENERGY_MOD = "energy_mod";
        public static final String WATER_MOD = "water_mod";
        public static final String RESIN_AMOUNT = "resin_amount";
        public static final String FLUID_AMOUNT_0 = "fluid_amount_0";
        public static final String FLUID_AMOUNT_1 = "fluid_amount_1";
        public static final String FLUID_AMOUNT_2 = "fluid_amount_2";
        public static final String CHANCE = "chance";
    }

    public enum RecipeParameterType {
        NUMBER,
        STRING,
        BOOLEAN,
        INT_LIST,
        EMPTY,
        MAP
    }
}