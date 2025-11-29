package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;

public class ExportButton extends CoreButton
{
    public static final Identifier BUTTON_TEXTURE = Identifier.fromMod("textures/gui/widgets/export_button.png");

    public ExportButton(String id, int x, int y, Runnable onPress, String tooltip)
    {
        super(id, x, y, 20, 20, "", onPress, tooltip);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        getRenderAdapter().drawTexture(ctx,
                                       BUTTON_TEXTURE,
                                       getBounds().getX(), getBounds().getY(),
                                       20, 20, 20, 60,
                                       0, isMouseOver(mouseX, mouseY) ? 20 : !isEnabled() ? 40 : 0,
                                       20, 20);
    }
}
