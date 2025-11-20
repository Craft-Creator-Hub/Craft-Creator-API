package fr.en0ri4n.craftcreator.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Pair<K, V>
{
    private final K first;
    private final V second;

    public static <K, V> Pair<K, V> of(K firstValue, V secondValue)
    {
        return new Pair<>(firstValue, secondValue);
    }
}
