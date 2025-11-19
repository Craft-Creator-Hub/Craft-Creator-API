package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;

/**
 * Platform-side helper for wiring core click actions to actual UI events.
 * Core never calls this directly to add buttons; loaders use it.
 *
 * <{@code
 * // Example: hook a vanilla button to a core click action
 * @Override
 * protected void init() {
 *     super.init();
 *     this.addRenderableWidget(new Button(this.width / 2 - 50, this.height / 2, 100, 20,
 *             new TextComponent("Export Recipes"),
 *             btn -> {
 *                 var platform = CraftCreatorAPI.getInstance().getPlatform();
 *                 var ui = platform.getUiAdapter();
 *                 var ctx = ui.buildContext(* pass e.g. this or player *);
 *                 var action = CraftCreatorGuiActions.runDatapackExport();
 *                 ui.schedule(action, ctx);
 *             }));
 * }
 * }
 */
public interface UiAdapter<T> {

    void openScreen(CoreScreenDefinition renderer);

    T createWidget(CoreUiElement element);
}