package fr.en0ri4n.craftcreator.platform.ui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreElementListener;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

public class ForgeButton extends Button implements CoreElementListener<CoreButton>
{
    private final CoreButton coreButton;

    public ForgeButton(CoreButton coreButton)
    {
        super(coreButton.getX(), coreButton.getY(),
              coreButton.getWidth(), coreButton.getHeight(),
              new TextComponent(coreButton.getLabel()), (btn) -> {});
        this.coreButton = coreButton;
        this.coreButton.setListener(this);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY)
    {
        this.coreButton.onClick((int) pMouseX, (int) pMouseY);
    }

    @Override
    public void update() {}

    @Override
    public CoreButton getElement()
    {
        return this.coreButton;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick)
    {
        this.coreButton.render(ForgeRenderContext.of(pPoseStack, pPartialTick), pMouseX, pMouseY);
    }
}
