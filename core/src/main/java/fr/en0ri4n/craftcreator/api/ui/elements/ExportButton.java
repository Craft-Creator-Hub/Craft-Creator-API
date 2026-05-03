package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.utils.Identifier;

public class ExportButton extends CoreButton
{
    public static final Identifier BUTTON_TEXTURE = Identifier.fromMod("textures/gui/widgets/export.png");

    public ExportButton(String id, Core2DBounds bounds, Runnable onPress, String tooltip)
    {
        super(id, bounds.getX(), bounds.getY(), 20, 20, "", onPress, tooltip);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        getRenderAdapter().drawTexture(ctx,
                                       CoreScreenDefinition.GUI_TEXTURE,
                                       getBounds().getX(), getBounds().getY(),
                                       20, 20, 16, 48,
                                       0, isMouseOver(mouseX, mouseY) ? 16 : !isEnabled() ? 32 : 0,
                                       16, 16);
        getRenderAdapter().drawTexture(ctx,
                                       BUTTON_TEXTURE,
                                       getBounds().getX(), getBounds().getY(),
                                       20, 20, 20, 20,
                                       0, 0,
                                       20, 20);
    }
}
