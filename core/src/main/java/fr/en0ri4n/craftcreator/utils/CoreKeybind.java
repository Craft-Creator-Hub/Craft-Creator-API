package fr.en0ri4n.craftcreator.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoreKeybind
{
    private final String name;
    private final int keyCode;
    private final String category;
    private Runnable onPress;
}
