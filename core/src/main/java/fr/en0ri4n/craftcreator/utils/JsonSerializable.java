package fr.en0ri4n.craftcreator.utils;

import com.google.gson.JsonObject;

public interface JsonSerializable
{
    void load(JsonObject jsonObject);

    JsonObject save();
}
