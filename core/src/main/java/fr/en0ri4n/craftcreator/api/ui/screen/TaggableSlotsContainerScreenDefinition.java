package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.TaggableSlotsBlockEntityBehavior;
import fr.en0ri4n.craftcreator.impl.model.screen.TagSelectionScreen;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;

public abstract class TaggableSlotsContainerScreenDefinition<T extends TaggableSlotsBlockEntityBehavior> extends CoreContainerScreenDefinition<T>
{
    public TaggableSlotsContainerScreenDefinition(ContainerModel<T> parent, T behavior, Identifier id, String title)
    {
        super(parent, behavior, id, title);
    }

    @Override
    public void renderBackground(RenderContext ctx)
    {
        super.renderBackground(ctx);
        renderOverlays(ctx);
    }

    @Override
    public void render(RenderContext ctx)
    {
        super.render(ctx);
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        if(!getCurrentUiAdapter().isCtrlKeyDown())
            return false;

        Pair<Integer, CoreItemStack> underMouse = getCurrentRenderAdapter().getItemStackUnderMouse(mouseX, mouseY);

        if(underMouse == null)
            return false;

        if(getParentContainerModel().getLayout().getSlot(underMouse.getFirst()).getType() != SlotDescriptor.SlotType.RECIPE_CREATOR_INPUT)
            return false;

        TagSelectionScreen tagSelectionScreen = new TagSelectionScreen(this, underMouse.getSecond());
        getCurrentUiAdapter().openScreen(tagSelectionScreen);
        return true;
    }

    private void renderOverlays(RenderContext ctx)
    {
        for(int slotIndex : getScreenData().getBehavior().getTaggedSlots().keySet())
        {
            SlotDescriptor slot = getParentContainerModel().getLayout().getSlot(slotIndex);

            if(slot == null)
                continue;

            renderTagOverlay(ctx, slot);
        }
    }

    private void renderTagOverlay(RenderContext ctx, SlotDescriptor slot)
    {
        int x = getGuiLeft() + slot.getX();
        int y = getGuiTop() + slot.getY();
        getCurrentRenderAdapter().drawRect(ctx, x, y, 16, 16, 0x80FF00FF); // semi-transparent magenta overlay
    }
}
