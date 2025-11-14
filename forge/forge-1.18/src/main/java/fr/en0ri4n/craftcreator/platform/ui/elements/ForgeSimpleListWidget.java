package fr.en0ri4n.craftcreator.platform.ui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.function.BiConsumer;

/**
 * Simple scrollable list bound to CoreList.
 */
public class ForgeSimpleListWidget extends ObjectSelectionList<ForgeSimpleListWidget.Entry> {

    private final CoreList coreList;
    private final BiConsumer<Integer, String> onSelect;

    public ForgeSimpleListWidget(Minecraft mc,
                                 int width,
                                 int height,
                                 int top,
                                 int bottom,
                                 int itemHeight,
                                 CoreList coreList,
                                 BiConsumer<Integer, String> onSelect) {
        super(mc, width, height, top, bottom, itemHeight);
        this.coreList = coreList;
        this.onSelect = onSelect;

        for (int i = 0; i < coreList.getEntries().size(); i++) {
            this.addEntry(new Entry(i, coreList.getEntries().get(i)));
        }
    }

    @Override
    public void setSelected(Entry entry) {
        super.setSelected(entry);
        if (entry == null) return;
        coreList.setSelectedIndex(entry.index);
        if (onSelect != null) {
            onSelect.accept(entry.index, entry.label);
        }
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {

        private final int index;
        private final String label;

        public Entry(int index, String label) {
            this.index = index;
            this.label = label;
        }

        @Override
        public void render(PoseStack poseStack, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float partialTicks) {
            Minecraft.getInstance().font.draw(poseStack, label, x + 2, y + 2, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ForgeSimpleListWidget.this.setSelected(this);
            return true;
        }

        @Override
        public Component getNarration() {
            return new TextComponent("");
        }
    }
}