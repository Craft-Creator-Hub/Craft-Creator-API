package fr.en0ri4n.craftcreator.impl.model.screen.minecraft;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTextInput;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.FurnaceRCBehavior;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.Arrays;

public class FurnaceRCScreenDefinition extends RecipeCreatorContainerScreenDefinition<FurnaceRCBehavior>
{
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromMod("textures/gui/container/minecraft/furnace_recipe_creator.png");

    private CoreDropdown<FurnaceRCBehavior.FurnaceType> furnaceTypeDropdown;
    private CoreTextInput cookingTimeInput;
    private CoreTextInput experienceInput;

    public FurnaceRCScreenDefinition(ContainerModel<FurnaceRCBehavior> parent)
    {
        super(parent, new FurnaceRCBehavior(), RecipeCreators.FURNACE_RECIPE_CREATOR_ID, "screen.minecraft_recipe_creator.smelting.title");
    }

    @Override
    public Identifier getBackgroundTexture()
    {
        return BACKGROUND_TEXTURE;
    }

    @Override
    public void initElements(int screenWidth, int screenHeight)
    {
        super.initElements(screenWidth, screenHeight);
        addElement(furnaceTypeDropdown = new CoreDropdown<>("furnace_type", getGuiSize().getRight(-22), getGuiSize().getY(), 22, 22, true, Arrays.stream(FurnaceRCBehavior.FurnaceType.values()).toList(), 0, "Furnace Type", this::setFurnaceType)
        {
            @Override
            public String getSelectedValueAsString()
            {
                return "";
            }
        });
    }

    @Override
    protected void addSettings()
    {
        addRecipeSetting(translate("screen.minecraft_recipe_creator.smelting.field.cooking_time"),
                         cookingTimeInput = new CoreTextInput("cooking_time_input", CoreTextInput.TextInputType.INTEGER, getGuiSize().getX(10), getGuiSize().getY(35), 30, 10, "", String.valueOf(200), "Ticks", "Time it takes to smelt one item in ticks"));
        addRecipeSetting(translate("screen.minecraft_recipe_creator.smelting.field.experience"),
                         experienceInput = new CoreTextInput("experience_input", CoreTextInput.TextInputType.FLOAT, getGuiSize().getX(10), getGuiSize().getY(60), 30, 10, "", String.valueOf(0.5F), "XP", "Experience awarded per item smelted"));
    }

    @Override
    protected boolean hasRecipeSettings()
    {
        return true;
    }

    private void setFurnaceType(FurnaceRCBehavior.FurnaceType type)
    {
        getScreenData().getBehavior().setFurnaceType(type);
    }

    @Override
    protected void fillBehavior(FurnaceRCBehavior behavior)
    {
        super.fillBehavior(behavior);
        behavior.setFurnaceType(furnaceTypeDropdown.getSelectedValue());
        behavior.setCookingTime(Integer.parseInt(cookingTimeInput.getValue()));
        behavior.setExperience(Float.parseFloat(experienceInput.getValue()));
    }

    @Override
    protected void onDataUpdated(FurnaceRCBehavior behavior)
    {
        super.onDataUpdated(behavior);
        furnaceTypeDropdown.setSelectedValue(behavior.getFurnaceType());
        cookingTimeInput.setValue(String.valueOf(behavior.getCookingTime()));
        experienceInput.setValue(String.valueOf(behavior.getExperience()));
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        super.renderForeground(ctx, mouseX, mouseY);
        getCurrentRenderAdapter().drawItem(ctx,
                                           new CoreItemStack(getBehavior().getFurnaceType().getItemId(), 1),
                                           furnaceTypeDropdown.getBounds().getX(3),
                                           furnaceTypeDropdown.getBounds().getY(3),
                                           1F);
    }
}
