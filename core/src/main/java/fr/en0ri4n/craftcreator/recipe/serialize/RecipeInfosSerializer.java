package fr.en0ri4n.craftcreator.recipe.serialize;

import com.google.gson.*;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos.*;
import fr.en0ri4n.craftcreator.serialize.JsonSerializer;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.*;

/**
 * JsonSerializer implementation for RecipeInfos, used by SerializerRegistry.
 */
public class RecipeInfosSerializer implements JsonSerializer<RecipeInfos> {

    @Override
    public JsonObject serialize(RecipeInfos value) {
        JsonObject jsonObject = new JsonObject();

        for (RecipeParameter parameter : value.getParameters()) {
            JsonObject parameterObj = new JsonObject();
            parameterObj.addProperty("type", parameter.getType().name());

            switch (parameter.getType()) {
                case NUMBER -> {
                    RecipeParameterNumber number = (RecipeParameterNumber) parameter;
                    parameterObj.addProperty("is_double", number.isDouble());
                    Number n = number.getNumberValue();
                    parameterObj.addProperty("value", number.isDouble()
                            ? n.doubleValue()
                            : n.intValue());
                }
                case BOOLEAN -> {
                    RecipeParameterBoolean bool = (RecipeParameterBoolean) parameter;
                    parameterObj.addProperty("value", bool.getBoolean());
                }
                case INT_LIST -> {
                    RecipeParameterIntList intList = (RecipeParameterIntList) parameter;
                    JsonArray jsonArray = new JsonArray();
                    intList.getList().forEach(jsonArray::add);
                    parameterObj.add("value", jsonArray);
                }
                case MAP -> {
                    RecipeParameterMap map = (RecipeParameterMap) parameter;
                    JsonObject obj = new JsonObject();
                    for (Map.Entry<Integer, Identifier> entry : map.getMap().entrySet()) {
                        obj.addProperty(entry.getKey().toString(), entry.getValue().toString());
                    }
                    parameterObj.add("value", obj);
                }
                case STRING, EMPTY -> {
                    // not used yet; keep for future
                }
            }

            jsonObject.add(parameter.getName(), parameterObj);
        }

        return jsonObject;
    }

    @Override
    public RecipeInfos deserialize(JsonObject element) {
        RecipeInfos infos = RecipeInfos.create();
        JsonObject jsonObject = element.getAsJsonObject();

        for (String key : jsonObject.entrySet().stream().map(Map.Entry::getKey).toList()) {
            JsonObject parameterCompound = jsonObject.get(key).getAsJsonObject();
            RecipeParameterType type = RecipeParameterType.valueOf(
                    parameterCompound.get("type").getAsString()
            );

            switch (type) {
                case NUMBER -> {
                    boolean isDouble = parameterCompound.get("is_double").getAsBoolean();
                    Number value = isDouble
                            ? parameterCompound.get("value").getAsDouble()
                            : parameterCompound.get("value").getAsInt();
                    infos.addParameter(new RecipeParameterNumber(key, value, isDouble));
                }
                case BOOLEAN -> {
                    infos.addParameter(new RecipeParameterBoolean(
                            key,
                            parameterCompound.get("value").getAsBoolean()
                    ));
                }
                case INT_LIST -> {
                    JsonArray array = parameterCompound.get("value").getAsJsonArray();
                    List<Integer> list = new ArrayList<>();
                    for (JsonElement e : array) list.add(e.getAsInt());
                    infos.addParameter(new RecipeParameterIntList(key, list));
                }
                case MAP -> {
                    JsonObject obj = parameterCompound.get("value").getAsJsonObject();
                    Map<Integer, Identifier> map = new HashMap<>();
                    for (String entryKey : obj.entrySet().stream().map(Map.Entry::getKey).toList()) {
                        map.put(Integer.valueOf(entryKey), Identifier.from(obj.get(entryKey).getAsString()));
                    }
                    infos.addParameter(new RecipeParameterMap(key, map));
                }
                case STRING, EMPTY -> {
                    // ignore for now
                }
            }
        }

        return infos;
    }
}