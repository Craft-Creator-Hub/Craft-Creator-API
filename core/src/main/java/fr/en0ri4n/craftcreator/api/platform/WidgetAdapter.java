package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;

public interface WidgetAdapter
{


    @FunctionalInterface
    interface ButtonAdapter<T>
    {
        T toPlatform(CoreButton coreButton);
    }

    @FunctionalInterface
    interface ListAdapter<T>
    {
        T toPlatform(CoreList coreButtons);
    }

    @FunctionalInterface
    interface DropdownAdapter<T>
    {
        T toPlatform(CoreDropdown coreDropdown);
    }

    @FunctionalInterface
    public interface TextInputAdapter<T>
    {
        T toPlatform(String coreTextInput);
    }
}
