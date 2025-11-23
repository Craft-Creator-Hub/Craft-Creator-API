package fr.en0ri4n.craftcreator.impl.model.screen.minecraft;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.WidgetRenderer;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CraftingTableRCBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.Arrays;

public class CraftingTableRCScreenDefinition extends RecipeCreatorContainerScreenDefinition<CraftingTableRCBehavior>
{
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromMod("textures/gui/container/minecraft/crafting_recipe_creator.png");

    private CoreDropdown<CraftingTableRCBehavior.CraftingType> shapeDropdown;

    public CraftingTableRCScreenDefinition(ContainerModel<CraftingTableRCBehavior> parent)
    {
        super(parent, new CraftingTableRCBehavior(), RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR, "Crafting Table Recipe Creator"); // TODO: localization
    }

    @Override
    public Identifier getBackgroundTexture()
    {
        return BACKGROUND_TEXTURE;
    }

    @Override
    public void init(WidgetRenderer renderer)
    {
        cleanScreen();
        addElement(shapeDropdown = new CoreDropdown<>("shape_type", getGuiLeft() + 100, getGuiTop() + 60, 50, 19, Arrays.stream(CraftingTableRCBehavior.CraftingType.values()).toList(), 0, "", this::setCraftingShape)
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
        addElement(new CoreButton("export", getGuiLeft() + 120, getGuiTop() + 10, 40, 19, "Save", this::exportRecipe, "Export the currently selected recipes"));
        super.init(renderer);
    }

    private void setCraftingShape()
    {
        getScreenData().getBehavior().setCraftingType(shapeDropdown.getSelectedValue());
        sendUpdates();
    }

    private void exportRecipe()
    {
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendMakeRecipeRequestToServer(new MakeRecipeRequestData(getParentContainerModel().getBlockEntityPos(), getId()));
    }

    @Override
    public void updateScreen(UiUpdateData data)
    {
        super.updateScreen(data);
        shapeDropdown.setSelectedValue(getScreenData().getBehavior().getCraftingType());
    }
}
