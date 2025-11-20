package fr.en0ri4n.craftcreator.api.ui.elements;

public interface CoreElementListener<T extends CoreUiElement>
{
    void update();
    T getElement();
}
