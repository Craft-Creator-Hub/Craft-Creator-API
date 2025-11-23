package fr.en0ri4n.craftcreator.platform.ui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreElementListener;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;

/**
 * Very simple dropdown: clicking cycles through options.
 */
public class ForgeDropdownWidget extends AbstractWidget implements CoreElementListener<CoreDropdown<?>>
{
    private final CoreDropdown<?> dropdown;

    public ForgeDropdownWidget(CoreDropdown<?> dropdown)
    {
        super(dropdown.getX(), dropdown.getY(), dropdown.getWidth(), dropdown.getHeight(), new TextComponent(dropdown.getSelectedValueAsString()));
        this.dropdown = dropdown;
        dropdown.setListener(this);
        updateMessage();
    }

    @Override
    public CoreDropdown<?> getElement()
    {
        return this.dropdown;
    }

    private void updateMessage()
    {
        this.setMessage(new TextComponent(dropdown.getSelectedValueAsString()));
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.dropdown.onClick((int) mouseX, (int) mouseY);
    }

    @Override
    public void update()
    {
        updateMessage();
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.dropdown.render(ForgeRenderContext.of(poseStack, partialTicks), mouseX, mouseY);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput)
    {
        // No narration for now
    }
}