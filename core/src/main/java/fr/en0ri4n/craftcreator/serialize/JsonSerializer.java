package fr.en0ri4n.craftcreator.serialize;

import com.google.gson.JsonObject;

public interface JsonSerializer<T> {
    JsonObject serialize(T value);
    T deserialize(JsonObject element);
}