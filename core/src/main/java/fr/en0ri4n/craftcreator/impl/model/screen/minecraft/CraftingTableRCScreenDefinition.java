package fr.en0ri4n.craftcreator.impl.model.screen.minecraft;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.ui.CoreContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.CraftingTableRCBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

public class CraftingTableRCScreenDefinition extends CoreContainerScreenDefinition<CraftingTableRCBehavior>
{
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromMod("textures/gui/container/minecraft/crafting_recipe_creator.png");

    private CoreDropdown shapeDropdown;

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
    public void init()
    {
        addElement(shapeDropdown = new CoreDropdown("shape_type", 10, 10, 100, 20, List.of("Shaped", "Shapeless"), getBehavior().isCraftingShapeless() ? 1 : 0, ""));
        addElement(new CoreButton("test", 120, 10, 10, 10, "S", "export_recipes", "Export the currently selected recipes"));
    }

    @Override
    public void updateScreen(UiUpdateData data)
    {
        System.out.println("Updating CraftingTableRCScreenDefinition with data: " + data);
        if(data.getPayload().has(CraftingTableRCBehavior.CRAFTING_TYPE_KEY)) {
            String type = data.getPayload().get(CraftingTableRCBehavior.CRAFTING_TYPE_KEY).getAsString();
            getBehavior().setCraftingShapeless(type.equals("shapeless"));
            shapeDropdown.setSelectedIndex(getBehavior().isCraftingShapeless() ? 1 : 0);
        }
    }

    @Override
    public void onButtonPressed(String elementId, String actionId)
    {
        System.out.println("Unknown button pressed. Element ID: " + elementId + ", Action ID: " + actionId);
    }

    @Override
    public void onDropdownChanged(String elementId, int index, String value)
    {
        if(elementId.equals(shapeDropdown.getId())) {
            System.out.println("Dropdown value changed: " + value);
            getBehavior().setCraftingShapeless(index == 1);
        }
    }
}
