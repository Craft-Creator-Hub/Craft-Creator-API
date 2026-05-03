package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.utils.Identifier;

public class RecipeSettingsButton extends CoreButton
{
    public static final Identifier BUTTON_TEXTURE = Identifier.fromMod("textures/gui/widgets/recipe_settings.png");

    public RecipeSettingsButton(String id, Core2DBounds bounds, Runnable onPress, String tooltip)
    {
        super(id, bounds.getX(), bounds.getY(), 20, 20, "", onPress, tooltip);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        getRenderAdapter().drawTexture(ctx,
                                       BUTTON_TEXTURE,
                                       getBounds().getX(), getBounds().getY(),
                                       20, 20, 40, 120,
                                       0, isMouseOver(mouseX, mouseY) ? 40 : !isEnabled() ? 80 : 0,
                                       40, 40);
    }
}
