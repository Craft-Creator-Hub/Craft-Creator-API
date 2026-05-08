package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;

/**
 * Platform-side helper for wiring core click actions to actual UI events.
 * Core never calls this directly to add buttons; loaders use it.
 */
public interface UiAdapter<T> {

    void openScreen(CoreScreenDefinition<?> renderer);

    boolean isCtrlKeyDown();

    T createWidget(CoreUiElement element);

    void playSound(UiSound sound);

    void closeScreen();

    enum UiSound
    {
        BUTTON_CLICK
    }
}