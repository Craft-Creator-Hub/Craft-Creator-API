package fr.en0ri4n.craftcreator.api.ui;

import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;

public interface CoreElementListener<T extends CoreUiElement>
{
    void update();
    T getElement();
}
