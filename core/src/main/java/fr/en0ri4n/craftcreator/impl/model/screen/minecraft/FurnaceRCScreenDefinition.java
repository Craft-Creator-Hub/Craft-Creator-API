package fr.en0ri4n.craftcreator.impl.model.screen.minecraft;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTextInput;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.FurnaceRCBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.Arrays;

public class FurnaceRCScreenDefinition extends RecipeCreatorContainerScreenDefinition<FurnaceRCBehavior>
{
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromMod("textures/gui/container/minecraft/furnace_recipe_creator.png");

    private CoreDropdown<FurnaceRCBehavior.FurnaceType> furnaceTypeDropdown;
    private CoreTextInput experienceInput;

    public FurnaceRCScreenDefinition(ContainerModel<FurnaceRCBehavior> parent)
    {
        super(parent, new FurnaceRCBehavior(), RecipeCreators.FURNACE_RECIPE_CREATOR, translate("screen.minecraft_recipe_creator.smelting.title"));
    }

    @Override
    public Identifier getBackgroundTexture()
    {
        return BACKGROUND_TEXTURE;
    }

    @Override
    public void initElements()
    {
        super.initElements();
        addElement(furnaceTypeDropdown = new CoreDropdown<>("furnace_type", getGuiSize().getX(100), getGuiSize().getY(60), 50, 19, Arrays.stream(FurnaceRCBehavior.FurnaceType.values()).toList(), 0, "", this::setFurnaceType)
        {
            @Override
            public String getSelectedValueAsString()
            {
                return switch(getSelectedValue()) {
                    case FURNACE -> "Furnace";
                    case BLAST_FURNACE -> "Blast Furnace";
                    case SMOKER -> "Smoker";
                };
            }
        });
        addElement(experienceInput = new CoreTextInput("experience_input", CoreTextInput.TextInputType.FLOAT, getGuiSize().getX(10), getGuiSize().getY(60), 30, 10, translate("screen.minecraft_recipe_creator.smelting.field.experience"), String.valueOf(0.5F), "XP", "Experience awarded per item smelted"));
    }

    private void setFurnaceType(FurnaceRCBehavior.FurnaceType type)
    {
        getScreenData().getBehavior().setFurnaceType(type);
        sendUpdates();
    }

    @Override
    protected CoreButton getExportButton()
    {
        return new CoreButton("export", getGuiSize().getX(120), getGuiSize().getY(10), 40, 19, "Save", this::exportRecipe, "Export the currently selected recipes");
    }

    @Override
    protected void onDataUpdated(FurnaceRCBehavior behavior)
    {
        super.onDataUpdated(behavior);
        furnaceTypeDropdown.setSelectedValue(behavior.getFurnaceType());
        experienceInput.setValue(String.valueOf(behavior.getExperience()));
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        CoreBounds bounds = CoreBounds.fromRight(getGuiSize().getRight(), getGuiSize().getY(), 22, 22);
        CoreScreenDefinition.renderTextureWithBounds(ctx, CoreScreenDefinition.GUI_TEXTURE, bounds, false, false);
        getCurrentRenderAdapter().drawItem(ctx, new CoreItemStack(getBehavior().getFurnaceType().getItemId(), 1), bounds.getX(3), bounds.getY(3), 1F);
    }
}
