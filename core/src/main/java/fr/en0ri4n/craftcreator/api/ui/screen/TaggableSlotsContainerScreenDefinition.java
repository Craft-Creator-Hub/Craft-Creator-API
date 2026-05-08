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
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        super.render(ctx, mouseX, mouseY);
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        Pair<Integer, CoreItemStack> underMouse = getRenderAdapter().getItemStackUnderMouse(mouseX, mouseY);

        if(!getCurrentUiAdapter().isCtrlKeyDown())
        {
            if(underMouse != null)
            {
                int slotIndex = underMouse.getFirst();
                getScreenData().getBehavior().getTaggedSlots().remove(slotIndex); // Remove tag from slot
            }

            // Return false to allow normal click processing (as we are just removing the tag)
            return false;
        }

        if(underMouse == null) return false;

        if(getParentContainerModel().getLayout().getSlot(underMouse.getFirst()).getType() != SlotDescriptor.SlotType.RECIPE_CREATOR_INPUT) return false;

        TagSelectionScreen tagSelectionScreen = new TagSelectionScreen(this, underMouse);
        getCurrentUiAdapter().openScreen(tagSelectionScreen);
        return true;
    }

    private void renderOverlays(RenderContext ctx)
    {
        for(int slotIndex : getScreenData().getBehavior().getTaggedSlots().keySet())
        {
            SlotDescriptor slot = getParentContainerModel().getLayout().getSlot(slotIndex);

            if(slot == null) continue;

            renderTagOverlay(ctx, slot);
        }
    }

    private void renderTagOverlay(RenderContext ctx, SlotDescriptor slot)
    {
        int x = getGuiSize().getX(slot.getX());
        int y = getGuiSize().getY(slot.getY());
        Identifier tag = getScreenData().getBehavior().getTaggedSlots().get(slot.getIndex());
        getRenderAdapter().drawRect(ctx, x, y, 16, 16, getColorForTag(tag.toString()));
    }

    /**
     * Generate a color based on the tag string. This ensures consistent coloring for the same tag.
     */
    private static int getColorForTag(String tag) {
        int hash = tag.hashCode();

        int a = 0xEE; // semi-transparent alpha
        int r = (hash >> 16) & 0xFF;
        int g = (hash >> 8)  & 0xFF;
        int b =  hash        & 0xFF;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
