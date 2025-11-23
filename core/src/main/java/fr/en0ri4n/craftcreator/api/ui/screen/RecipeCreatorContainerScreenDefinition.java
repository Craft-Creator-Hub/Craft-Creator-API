package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.api.mod.SupportedSerializationTypes;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.RecipeCreatorBlockEntityBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.Arrays;

public abstract class RecipeCreatorContainerScreenDefinition<T extends RecipeCreatorBlockEntityBehavior> extends TaggableSlotsContainerScreenDefinition<T>
{
    private CoreDropdown<SupportedSerializationTypes> recipeTypeDropdown;

    public RecipeCreatorContainerScreenDefinition(ContainerModel<T> parent, T behavior, Identifier id, String title)
    {
        super(parent, behavior, id, title);
    }

    @Override
    public void init(WidgetRenderer widgetRenderer)
    {
        addElement(recipeTypeDropdown = new CoreDropdown<>("recipe_type_dropdown", getGuiLeft(), getGuiTop(), 20, 20, Arrays.stream(SupportedSerializationTypes.values()).toList(), 0,
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
        super.init(widgetRenderer);
    }

    @Override
    public void updateScreen(UiUpdateData data)
    {
        super.updateScreen(data);
        recipeTypeDropdown.setSelectedValue(getScreenData().getBehavior().getSerializationType());
    }

    private void onRecipeTypeChanged()
    {
        getScreenData().getBehavior().setSerializationType(recipeTypeDropdown.getSelectedValue());
        sendUpdates();
    }
}
