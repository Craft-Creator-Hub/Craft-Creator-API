package fr.en0ri4n.craftcreator.platform.ui.container;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.*;
import fr.en0ri4n.craftcreator.impl.model.container.minecraft.CraftingTableRecipeCreatorContainerModel;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderAdapter;
import fr.en0ri4n.craftcreator.platform.render.ForgeRenderContext;
import fr.en0ri4n.craftcreator.platform.ui.ForgeDropdownWidget;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeSimpleListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class ForgeRecipeCreatorScreen extends AbstractContainerScreen<ForgeRecipeCreatorMenu>
{

    private final ContainerModel model;

    public ForgeRecipeCreatorScreen(ForgeRecipeCreatorMenu menu, Inventory playerInv, Component title)
    {
        this(menu, playerInv, title, new CraftingTableRecipeCreatorContainerModel());
    }

    public ForgeRecipeCreatorScreen(ForgeRecipeCreatorMenu menu, Inventory playerInv, Component title, ContainerModel model)
    {
        super(menu, playerInv, title);
        this.model = model;
        this.imageWidth = model.getLayout().getWidth();
        this.imageHeight = model.getLayout().getHeight();
    }

    @Override
    protected void init()
    {
        super.init();

        model.getScreenDefinition().init();

        for(CoreUiElement element : model.getScreenDefinition().getElements())
        {
            switch(element.getType())
            {
                case BUTTON:
                    addButtonWidget((CoreButton) element);
                    break;
                case TEXT_INPUT:
                    addTextInputWidget((CoreTextInput) element);
                    break;
                case DROPDOWN:
                    addDropdownWidget((CoreDropdown) element);
                    break;
                case LIST:
                    addListWidget((CoreList) element);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + model.getScreenDefinition().getElements().stream().map(CoreUiElement::getType));
            }
        }
    }

    private void addButtonWidget(CoreButton btn)
    {
        int x = this.leftPos + btn.getX();
        int y = this.topPos + btn.getY();
        int w = btn.getWidth();
        int h = btn.getHeight();

        Button mcButton = new Button(x, y, w, h, new TextComponent(btn.getLabel()), b -> model.onButtonPressed(btn.getId(), btn.getActionId()));
        mcButton.active = btn.isEnabled();
        this.addRenderableWidget(mcButton);
    }

    private void addTextInputWidget(CoreTextInput text)
    {
        int x = this.leftPos + text.getX();
        int y = this.topPos + text.getY();
        int w = text.getWidth();
        int h = text.getHeight();

        EditBox box = new EditBox(this.font, x, y, w, h, new TextComponent(""));
        box.setMaxLength(text.getMaxLength());
        box.setValue(text.getValue() != null ? text.getValue() : "");
        this.addRenderableWidget(box);
    }

    private void addDropdownWidget(CoreDropdown dropdown)
    {
        int x = this.leftPos + dropdown.getX();
        int y = this.topPos + dropdown.getY();
        int w = dropdown.getWidth();
        int h = dropdown.getHeight();

        ForgeDropdownWidget widget = new ForgeDropdownWidget(x, y, w, h, dropdown, (selectedIndex, selectedValue) ->
        {
            dropdown.setSelectedIndex(selectedIndex);
            model.onDropdownChanged(dropdown.getId(), selectedIndex, selectedValue);
        });
        this.addRenderableWidget(widget);
    }

    private void addListWidget(CoreList list)
    {
        int x = list.getX();
        int y = list.getY();
        int w = list.getWidth();
        int h = list.getHeight();

        ForgeSimpleListWidget widget = new ForgeSimpleListWidget(Minecraft.getInstance(), w, h, y, y + h, 12, list, (selectedIndex, value) ->
        {
            list.setSelectedIndex(selectedIndex);
            // if you want, add handler callback here too
        });
        widget.setLeftPos(x);
        this.addRenderableWidget(widget);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        ForgeRenderContext ctx = new ForgeRenderContext(poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), partialTicks);
        model.getScreenDefinition().renderBackground(ctx, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }
}