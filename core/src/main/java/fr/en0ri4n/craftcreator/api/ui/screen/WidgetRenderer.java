package fr.en0ri4n.craftcreator.api.ui.screen;

public interface WidgetRenderer
{
    /**
     * Add a widget to the screen.
     * If you are implementing this interface, you need to cast the widget to the appropriate type.
     * But the widget parameter is kept as Object to avoid platform-specific dependencies in the API.
     * This object will always be of the type corresponding to the platform's UI framework.
     * @see fr.en0ri4n.craftcreator.api.platform.UiAdapter#createWidget
     */
    void addWidgetToScreen(Object widget);
}
