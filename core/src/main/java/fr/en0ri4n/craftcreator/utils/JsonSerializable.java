package fr.en0ri4n.craftcreator.utils;

import com.google.gson.JsonObject;

public interface JsonSerializable
{
    void deserialize(JsonObject jsonObject);

    JsonObject serialize();
}
