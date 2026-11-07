package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
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
        if(!isVisible()) return;

        CoreButtonStyle.State state = CoreButtonStyle.State.of(isEnabled(), false, isMouseOver(mouseX, mouseY));
        CoreButtonStyle.draw(ctx, getBounds(), state);
        getRenderAdapter().drawTexture(ctx,
                                       BUTTON_TEXTURE,
                                       getBounds().getX(), getBounds().getY() + state.getContentOffsetY(),
                                       20, 20, 20, 20,
                                       0, 0,
                                       20, 20);
    }
}
