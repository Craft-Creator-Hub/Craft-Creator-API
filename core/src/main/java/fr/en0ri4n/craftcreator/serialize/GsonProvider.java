package fr.en0ri4n.craftcreator.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;

public final class GsonProvider {

    private static final Gson GSON = new GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .setPrettyPrinting()
            .create();

    private GsonProvider() {}

    public static Gson gson() {
        return GSON;
    }
}