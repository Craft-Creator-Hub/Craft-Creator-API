package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.ExportButton;
import fr.en0ri4n.craftcreator.api.ui.elements.RecipeSettingsButton;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.Arrays;

public abstract class RecipeCreatorContainerScreenDefinition<T extends RecipeCreatorBlockEntityBehavior> extends TaggableSlotsContainerScreenDefinition<T>
{
    private CoreDropdown<SupportedRecipeExporter> recipeTypeDropdown;

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
        addElement(new RecipeSettingsButton("recipe_settings_button", getRecipeSettingsButtonBounds(), this::openRecipeSettingsScreen, "Recipe Settings"));
        addElement(new ExportButton("export_button", getExportButtonBounds(), this::exportRecipe, "Export Recipe"));
    }

    protected Core2DBounds getRecipeSettingsButtonBounds()
    {
        return Core2DBounds.ofPos(getGuiSize().getX(20), getGuiSize().getY(60));
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
        sendUpdates();
    }

    protected void openRecipeSettingsScreen()
    {
        // By default, do nothing as not all recipe creators will have settings. Specific implementations can override this to open a settings screen.
    }

    protected void exportRecipe()
    {
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendMakeRecipeRequestToServer(new MakeRecipeRequestData(getParentContainerModel().getBlockEntityPos(), getId()));
    }

}
