package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.platform.UiAdapter;
import fr.en0ri4n.craftcreator.api.ui.ClickContext;
import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.screen.ForgeScreenRenderer;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.client.Minecraft;

public class ForgeUiAdapter implements UiAdapter {

    @Override
    public void openScreen(CoreScreenDefinition screenDefinition) {
        Minecraft.getInstance().tell(() ->
                Minecraft.getInstance().setScreen(new ForgeScreenRenderer(screenDefinition))
        );
    }

    @Override
    public ClickContext buildContext(Object raw) {
        // raw could be e.g. a player instance, screen, click packet, etc.
        // You decide per usage. Example: wrap player id only.
        return new ClickContext() {
            @Override
            public Identifier getTargetBlockId() {
                return null; // fill in if you need it
            }
        };
    }

    @Override
    public void schedule(ClickContext.ClickActionWithContext action, ClickContext ctx) {
        // Usually just run on main thread; use Minecraft/Forge dispatcher if needed
        action.execute(ctx);
    }
}