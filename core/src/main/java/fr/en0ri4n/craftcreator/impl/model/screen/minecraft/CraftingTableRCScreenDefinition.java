package fr.en0ri4n.craftcreator.impl.model.screen.minecraft;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CraftingTableRCBehavior;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.Arrays;

public class CraftingTableRCScreenDefinition extends RecipeCreatorContainerScreenDefinition<CraftingTableRCBehavior>
{
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromMod("textures/gui/container/minecraft/crafting_recipe_creator.png");

    private CoreDropdown<CraftingTableRCBehavior.CraftingType> shapeDropdown;

    public CraftingTableRCScreenDefinition(ContainerModel<CraftingTableRCBehavior> parent)
    {
        super(parent, new CraftingTableRCBehavior(), RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR_ID, "screen.minecraft_recipe_creator.crafting.title");
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
        addElement(shapeDropdown = new CoreDropdown<>("shape_type", getGuiSize().getX(100), getGuiSize().getY(60), 50, 19, false, Arrays.stream(CraftingTableRCBehavior.CraftingType.values()).toList(), 0, "", this::setCraftingShape)
        {
            @Override
            public String getSelectedValueAsString()
            {
                return switch(getSelectedValue()) {
                    case SHAPED -> "Shaped";
                    case SHAPELESS -> "Shapeless";
                };
            }
        });
    }

    private void setCraftingShape(CraftingTableRCBehavior.CraftingType type)
    {
        getScreenData().getBehavior().setCraftingType(type);
        sendUpdates();
    }

    @Override
    protected void onDataUpdated(CraftingTableRCBehavior behavior)
    {
        super.onDataUpdated(behavior);
        shapeDropdown.setSelectedValue(behavior.getCraftingType());
    }
}
