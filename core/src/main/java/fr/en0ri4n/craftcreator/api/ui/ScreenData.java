package fr.en0ri4n.craftcreator.api.ui;

import com.google.gson.JsonObject;

public interface ScreenData
{
    void load(JsonObject payload);

    JsonObject save();
}
