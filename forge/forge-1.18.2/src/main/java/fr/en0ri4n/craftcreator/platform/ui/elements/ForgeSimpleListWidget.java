package fr.en0ri4n.craftcreator.platform.ui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreElementListener;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

/**
 * Simple, pretty list widget for Forge screens.
 * <p>
 * - Uses ObjectSelectionList so scrolling/selection/keyboard navigation work out of the box.
 * - Draws a soft gradient background, subtle border and rounded-like selection highlight.
 * - Entries can show text and an optional icon (ItemStack or texture ResourceLocation).
 *
 */
public class ForgeSimpleListWidget extends ObjectSelectionList<ForgeSimpleListWidget.ForgeSimpleListEntry> implements CoreElementListener<CoreList>
{
    private final CoreList listModel;

    /**
     * Create the list widget.
     *
     * @param listModel The core list model to back this widget.
     */
    public ForgeSimpleListWidget(CoreList listModel)
    {
        super(Minecraft.getInstance(), listModel.getWidth(), listModel.getHeight(),
                listModel.getY(), listModel.getY() + listModel.getHeight(),
                listModel.getItemHeight());
        this.listModel = listModel;
        this.x0 = listModel.getX();
        this.x1 = listModel.getX() + listModel.getWidth();
        this.listModel.setListener(this);
    }

    @Override
    public CoreList getElement()
    {
        return this.listModel;
    }

    @Override
    public void update()
    {
        replaceEntries(this.listModel.getEntries().stream().map(ForgeSimpleListEntry::new).toList());
        if(this.listModel.getSelectedIndex() >= 0) setSelected(getEntry(this.listModel.getSelectedIndex()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return this.listModel.mouseClicked((int) mouseX, (int) mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.listModel.render(ForgeRenderContext.of(poseStack, partialTicks), mouseX, mouseY);
    }

    /**
     * Entry type for the list. Simple text + optional icon (ItemStack or texture).
     */
    public static class ForgeSimpleListEntry extends ObjectSelectionList.Entry<ForgeSimpleListEntry>
    {
        private final CoreList.Entry entryModel;

        public ForgeSimpleListEntry(CoreList.Entry entryModel)
        {
            this.entryModel = entryModel;
        }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks)
        {
            // Not used, rendering is handled by CoreList.Entry
        }

        @Override
        public Component getNarration()
        {
            return new TextComponent(this.entryModel.getLabel());
        }
    }
}