package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButtonWidgetList;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.api.ui.elements.ExportButton;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.Arrays;

public abstract class RecipeCreatorContainerScreenDefinition<T extends RecipeCreatorBlockEntityBehavior> extends TaggableSlotsContainerScreenDefinition<T>
{
    private CoreDropdown<SupportedRecipeExporter> recipeTypeDropdown;
    private CoreButtonWidgetList settingsList;

    public RecipeCreatorContainerScreenDefinition(ContainerModel<T> parent, T behavior, Identifier id, String title)
    {
        super(parent, behavior, id, title);
    }

    @Override
    public void initElements(int screenWidth, int screenHeight)
    {
        addElement(recipeTypeDropdown = new CoreDropdown<>("recipe_type_dropdown", getGuiSize().getX(), getGuiSize().getY(), 20, 20, false, Arrays.stream(SupportedRecipeExporter.values()).toList(), 0,
                "Recipe Type", this::onRecipeTypeChanged) {
            @Override
            public String getSelectedValueAsString()
            {
                return switch(getSelectedValue()) {
                    case MINECRAFT_DATAPACK -> "MC";
                    case KUBE_JS -> "KJS";
                };
            }
        });
        //addElement(new RecipeSettingsButton("recipe_settings_button", getRecipeSettingsButtonBounds(), this::openRecipeSettingsScreen, "Recipe Settings"));
        addElement(new ExportButton("export_button", getExportButtonBounds(), this::exportRecipe, "Export Recipe"));

        addElement(settingsList = new CoreButtonWidgetList("example_button_list", getGuiSize().getX(-100), getGuiSize().getY(), 100, 100, "HELLO"));
        addSettings();
    }

    /**
     * Allows adding custom settings buttons to the screen. By default, does nothing as not all recipe creators will have settings. Specific implementations can override this to add settings buttons to the settings list.
     */
    protected void addSettings() { /* By default, do nothing. Specific implementations can override this to add settings buttons to the settings list. */ }

    /**
     * Helper method to add a setting button to the settings list. The label will be displayed on the left of the button, and the element can be any CoreUiElement (e.g. a text input, a dropdown, etc.).<br>
     * This method need to be called inside the addSettings() method, and the settings list will automatically handle the positioning of the elements.
     * @param label The label to display on the left of the setting element
     * @param element The setting element to display on the right of the label. Can be any CoreUiElement (e.g. a text input, a dropdown, etc.)
     */
    protected void addRecipeSetting(String label, CoreUiElement element)
    {
        settingsList.addWidget(label, element);
    }

    protected Core2DBounds getExportButtonBounds()
    {
        return Core2DBounds.ofPos(getGuiSize().getRight(-26), getGuiSize().getY(60));
    }

    @Override
    protected void onDataUpdated(T behavior)
    {
        recipeTypeDropdown.setSelectedValue(getScreenData().getBehavior().getSerializationType());
    }

    private void onRecipeTypeChanged(SupportedRecipeExporter recipeExporter)
    {
        getScreenData().getBehavior().setSerializationType(recipeExporter);
    }

    protected void exportRecipe()
    {
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendMakeRecipeRequestToServer(new MakeRecipeRequestData(getParentContainerModel().getBlockEntityPos(), getId()));
    }

}
