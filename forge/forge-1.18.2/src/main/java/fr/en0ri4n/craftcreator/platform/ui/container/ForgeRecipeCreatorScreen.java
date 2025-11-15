package fr.en0ri4n.craftcreator.platform.ui.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.*;
import fr.en0ri4n.craftcreator.impl.model.container.minecraft.CraftingTableRecipeCreatorContainerModel;
import fr.en0ri4n.craftcreator.platform.ui.ForgeDropdownWidget;
import fr.en0ri4n.craftcreator.platform.ui.elements.ForgeSimpleListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Objects;

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

        // Centering / scaling strategy is up to you; here we use core coordinates directly.
        for(CoreUiElement element : model.getScreenDefinition().getElements())
        {
            if(element instanceof CoreButton btn)
            {
                addButtonWidget(btn);
            }
            else if(element instanceof CoreTextInput text)
            {
                addTextInputWidget(text);
            }
            else if(element instanceof CoreDropdown dropdown)
            {
                addDropdownWidget(dropdown);
            }
            else if(element instanceof CoreList list)
            {
                addListWidget(list);
            }
        }
    }

    private void addButtonWidget(CoreButton btn)
    {
        int x = btn.getX();
        int y = btn.getY();
        int w = btn.getWidth();
        int h = btn.getHeight();

        Button mcButton = new Button(x, y, w, h, new TextComponent(btn.getLabel()), b ->
        {
            model.onButtonPressed(btn.getId(), btn.getActionId());
        });
        mcButton.active = btn.isEnabled();
        this.addRenderableWidget(mcButton);
    }

    private void addTextInputWidget(CoreTextInput text)
    {
        int x = text.getX();
        int y = text.getY();
        int w = text.getWidth();
        int h = text.getHeight();

        EditBox box = new EditBox(this.font, x, y, w, h, new TextComponent(""));
        box.setMaxLength(text.getMaxLength());
        box.setValue(text.getValue() != null ? text.getValue() : "");
        this.addRenderableWidget(box);
    }

    private void addDropdownWidget(CoreDropdown dropdown)
    {
        int x = dropdown.getX();
        int y = dropdown.getY();
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
        model.getScreenDefinition().renderBackground(this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }
}