package fr.en0ri4n.craftcreator.serialize;

import java.util.HashMap;
import java.util.Map;

public final class SerializerRegistry {

    private static final Map<Class<?>, JsonSerializer<?>> BY_CLASS = new HashMap<>();

    private SerializerRegistry() {}

    public static <T> void register(Class<T> type, JsonSerializer<T> serializer) {
        BY_CLASS.put(type, serializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> JsonSerializer<T> get(Class<T> type) {
        return (JsonSerializer<T>) BY_CLASS.get(type);
    }
}