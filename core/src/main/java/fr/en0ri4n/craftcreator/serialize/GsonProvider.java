package fr.en0ri4n.craftcreator.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GsonProvider {

    private static final Gson PRETTY_GSON = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create();

    private static final Gson COMPACT_GSON = new GsonBuilder()
            .setLenient()
            .create();

    public static Gson prettyGson() {
        return PRETTY_GSON;
    }

    public static Gson compactGson() {
        return COMPACT_GSON;
    }
}