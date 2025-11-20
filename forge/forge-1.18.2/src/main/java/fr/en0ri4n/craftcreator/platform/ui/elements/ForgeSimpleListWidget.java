package fr.en0ri4n.craftcreator.platform.ui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreElementListener;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

/**
 * Simple scrollable list bound to CoreList.
 */
public class ForgeSimpleListWidget extends ObjectSelectionList<ForgeSimpleListWidget.Entry> implements CoreElementListener<CoreList>
{
    private final CoreList coreList;

    public ForgeSimpleListWidget(int width, int height, int top, int bottom, int itemHeight, CoreList coreList)
    {
        super(Minecraft.getInstance(), width, height, top, bottom, itemHeight);
        this.coreList = coreList;
        for(int i = 0; i < coreList.getEntries().size(); i++)
            this.addEntry(new Entry(i, coreList.getEntries().get(i)));
        this.coreList.setListener(this);
    }

    @Override
    public void setSelected(Entry entry)
    {
        super.setSelected(entry);
        if(entry == null) return;
        coreList.setSelectedIndex(entry.index);
    }

    @Override
    public void update()
    {
        clearEntries();
        for(int i = 0; i < getElement().getEntries().size(); i++)
            this.addEntry(new Entry(i, getElement().getEntries().get(i)));
        setSelected(getEntry(getElement().getSelectedIndex()));
    }

    @Override
    public CoreList getElement()
    {
        return coreList;
    }

    public class Entry extends ObjectSelectionList.Entry<Entry>
    {
        private final int index;
        private final String label;

        public Entry(int index, String label)
        {
            this.index = index;
            this.label = label;
        }

        @Override
        public void render(PoseStack poseStack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float partialTicks)
        {
            Minecraft.getInstance().font.draw(poseStack, label, x + 2, y + 2, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            ForgeSimpleListWidget.this.setSelected(this);
            return true;
        }

        @Override
        public Component getNarration()
        {
            return new TextComponent("");
        }
    }
}