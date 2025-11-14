package fr.en0ri4n.craftcreator.serialize;

import com.google.gson.JsonElement;

public interface JsonSerializer<T> {
    JsonElement serialize(T value);
    T deserialize(JsonElement element);
}