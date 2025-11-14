package fr.en0ri4n.craftcreator.api.ui.recipe;

import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import lombok.Getter;

import java.util.List;

@Getter
public class RecipeCreatorContainerModel implements ContainerModel {

    private final ContainerLayout layout;
    private final CoreScreenDefinition screen;

    public RecipeCreatorContainerModel() {
        this.layout = fr.en0ri4n.craftcreator.ui.container.RecipeCreatorContainerLayouts.basicRecipeEditor();
        this.screen = getScreenDefinition();
    }

    @Override
    public CoreScreenDefinition getScreenDefinition() {
        CoreScreenDefinition def = new CoreScreenDefinition("recipe_creator", "Recipe Creator");

        def.addElement(new CoreDropdown(
                "mod_selector", 10, 10, 100, 20,
                List.of("Minecraft", "Create", "Botania", "Thermal"), -1, ""
        ));

        def.addElement(new CoreButton(
                "export_button", 10, 40, 80, 20,
                "Export", "export_recipes", "Export the currently selected recipes"
        ));

        // add more UI elements as needed
        return def;
    }

    @Override
    public void onButtonAction(String elementId, String actionId) {
        if ("export_recipes".equals(actionId)) {
            // call core logic to export recipes using current selection
        }
    }

    @Override
    public void onDropdownChanged(String elementId, int index, String value) {
        if ("mod_selector".equals(elementId)) {
            // update available recipes based on selected mod
        }
    }
}