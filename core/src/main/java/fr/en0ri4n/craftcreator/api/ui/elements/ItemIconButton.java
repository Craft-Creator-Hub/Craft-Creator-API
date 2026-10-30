package fr.en0ri4n.craftcreator.api.ui.elements;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Setter;

/**
 * Square button showing an item, e.g. a tab to pick a recipe type. A selected button is drawn pressed and can't be clicked.
 */
public class ItemIconButton extends CoreButton
{
    public static final int SIZE = 20;

    private final CoreItemStack icon;
    @Setter
    private boolean selected;

    public ItemIconButton(String id, int x, int y, Identifier itemId, Runnable onPress, String tooltip)
    {
        super(id, x, y, SIZE, SIZE, "", onPress, tooltip);
        this.icon = new CoreItemStack(itemId, 1);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        return !selected && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
    {
        if(!isVisible()) return;

        CoreScreenDefinition.renderTextureWithBounds(ctx, CoreScreenDefinition.GUI_TEXTURE, getBounds(), !selected && isMouseOver(mouseX, mouseY), selected);
        getRenderAdapter().drawItem(ctx, icon, getBounds().getX(2), getBounds().getY(2), 1F);
    }
}
