package fr.en0ri4n.craftcreator.platform.ui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.CoreElementListener;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;

import java.util.function.BiConsumer;

/**
 * Very simple dropdown: clicking cycles through options.
 */
public class ForgeDropdownWidget extends AbstractWidget implements CoreElementListener<CoreDropdown>
{
    private final CoreDropdown dropdown;
    private final BiConsumer<Integer, String> onChange;

    public ForgeDropdownWidget(int x, int y, int width, int height, CoreDropdown dropdown, BiConsumer<Integer, String> onChange)
    {
        super(x, y, width, height, new TextComponent(""));
        this.dropdown = dropdown;
        this.onChange = onChange;
        dropdown.setListener(this);
        updateMessage();
    }

    @Override
    public CoreDropdown getElement()
    {
        return this.dropdown;
    }

    private void updateMessage()
    {
        String text = dropdown.getSelectedValue();
        if(text == null) text = "<none>";
        this.setMessage(new TextComponent(text));
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        if(!this.active) return;

        int size = dropdown.getOptions().size();
        if(size == 0) return;

        int current = dropdown.getSelectedIndex();
        int next = (current + 1) % size;
        dropdown.setSelectedIndex(next);
        updateMessage();
        if(onChange != null)
            onChange.accept(next, dropdown.getSelectedValue());
    }

    @Override
    public void update()
    {
        updateMessage();
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        // Basic button-like background
        Minecraft mc = Minecraft.getInstance();
        int bgColor = this.isHoveredOrFocused() ? 0xFF777777 : 0xFF555555;
        fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, bgColor);

        // Draw label centered
        int color = 0xFFFFFFFF;
        int textX = this.x + 4;
        int textY = this.y + (this.height - 8) / 2;
        mc.font.draw(poseStack, this.getMessage(), textX, textY, color);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput)
    {
        // No narration for now
    }
}