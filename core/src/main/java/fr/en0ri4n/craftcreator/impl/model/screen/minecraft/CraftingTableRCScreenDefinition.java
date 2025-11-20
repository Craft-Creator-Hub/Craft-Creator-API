package fr.en0ri4n.craftcreator.impl.model.screen.minecraft;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.WidgetRenderer;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CraftingTableRCBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

public class CraftingTableRCScreenDefinition extends RecipeCreatorContainerScreenDefinition<CraftingTableRCBehavior>
{
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromMod("textures/gui/container/minecraft/crafting_recipe_creator.png");

    private CoreDropdown shapeDropdown;
    private CoreButton exportButton;

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
        addElement(shapeDropdown = new CoreDropdown("shape_type", 10, 10, 100, 20, List.of("Shaped", "Shapeless"), 0, ""));
        addElement(exportButton = new CoreButton("export", 120, 10, 10, 10, "S", "export_recipes", "Export the currently selected recipes"));
        super.init(renderer);
    }

    @Override
    public void updateScreen(UiUpdateData data)
    {
        super.updateScreen(data);
        shapeDropdown.setSelectedIndex(getScreenData().getBehavior().isCraftingShapeless() ? 1 : 0);
    }

    @Override
    public void onButtonPressed(String elementId, String actionId)
    {
        if(elementId.equals(exportButton.getId())) {
            // Trigger export
        }
    }


    @Override
    public void onDropdownChanged(String elementId, int index, String value)
    {
        if(elementId.equals(shapeDropdown.getId())) {
            getScreenData().getBehavior().setCraftingShapeless(index == 1);
        }
    }
}
