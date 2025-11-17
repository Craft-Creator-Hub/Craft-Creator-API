package fr.en0ri4n.craftcreator.impl.model.screen.minecraft;

import fr.en0ri4n.craftcreator.RecipeCreators;
import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

public class CraftingTableScreenDefinition extends CoreScreenDefinition
{
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromMod("textures/gui/container/minecraft/crafting_recipe_creator.png");

    private static final String EXPORT_BUTTON_ID = "export_button";

    public CraftingTableScreenDefinition()
    {
        super(RecipeCreators.CRAFTING_TABLE_RECIPE_CREATOR.toString(), "Crafting Table Recipe Creator"); // TODO: localization
    }

    @Override
    public Identifier getBackgroundTexture()
    {
        return BACKGROUND_TEXTURE;
    }

    @Override
    public void init()
    {
        addElement(new CoreDropdown("mod_selector", 10, 10, 100, 20, List.of("Shaped", "Shapeless"), 0, ""));
        addElement(new CoreButton(EXPORT_BUTTON_ID, 120, 10, 10, 10, "S", "export_recipes", "Export the currently selected recipes"));
    }

    @Override
    public void onButtonPressed(String elementId, String actionId)
    {
        if(elementId.equals(EXPORT_BUTTON_ID))
        {
            System.out.println("Export button pressed. Action ID: " + actionId);
        }
        else
        {
            System.out.println("Unknown button pressed. Element ID: " + elementId + ", Action ID: " + actionId);
        }

    }
}
