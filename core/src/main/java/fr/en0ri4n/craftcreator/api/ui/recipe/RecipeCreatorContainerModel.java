package fr.en0ri4n.craftcreator.api.ui.recipe;

import fr.en0ri4n.craftcreator.api.blockentity.CoreBlockEntity;
import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerLayout;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;
import lombok.Getter;

import java.util.List;

/**
 * Container model for the recipe creator block.
 * Provides a 10-slot inventory and a dropdown to select recipe type (Shaped/Shapeless).
 */
@Getter
public class RecipeCreatorContainerModel implements ContainerModel {

    private final CoreBlockEntity coreEntity;
    private final ContainerLayout layout;
    private final RecipeCreatorScreenDefinition screenDefinition;

    public RecipeCreatorContainerModel(CoreBlockEntity coreEntity) {
        this.coreEntity = coreEntity;
        this.layout = createLayout();
        this.screenDefinition = new RecipeCreatorScreenDefinition(this);
    }

    private ContainerLayout createLayout() {
        ContainerLayout layout = new ContainerLayout();
        
        // 10 slots for recipe creator inventory (2 rows of 5)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                int index = row * 5 + col;
                layout.addSlot(new SlotDescriptor(
                    SlotDescriptor.SlotType.CUSTOM,
                    8 + col * 18,
                    17 + row * 18,
                    index,
                    Identifier.fromMod("recipe_creator_slot_" + index)
                ));
            }
        }

        // Add player inventory slots
        ContainerModel.addPlayerInventorySlots(layout, 8, 84);

        return layout;
    }

    @Override
    public ContainerLayout getLayout() {
        return layout;
    }

    @Override
    public CoreScreenDefinition getScreenDefinition() {
        return screenDefinition;
    }

    @Override
    public void onButtonPressed(String elementId, String actionId) {
        // No buttons for now
    }

    @Override
    public void onDropdownChanged(String elementId, int index, String value) {
        if ("recipe_type_dropdown".equals(elementId) && value != null) {
            // Save the selected recipe type to the core entity's extraData
            coreEntity.getExtraData().addProperty("recipe_type", value);
        }
    }

    /**
     * Inner screen definition for the recipe creator UI.
     */
    public static class RecipeCreatorScreenDefinition extends CoreScreenDefinition {
        
        private final RecipeCreatorContainerModel model;

        public RecipeCreatorScreenDefinition(RecipeCreatorContainerModel model) {
            super("recipe_creator", "Recipe Creator");
            this.model = model;
        }

        @Override
        public Identifier getBackgroundTexture() {
            return Identifier.fromMod("textures/gui/container/recipe_creator.png");
        }

        @Override
        public Pair<Integer, Integer> getBackgroundTextureSize() {
            return Pair.create(176, 166);
        }

        @Override
        public void init() {
            // Add dropdown for recipe type selection
            CoreDropdown dropdown = new CoreDropdown(
                "recipe_type_dropdown",
                10, 60,
                100, 20,
                List.of("Shaped", "Shapeless"),
                0, // Default to first option (Shaped)
                "Select recipe type"
            );
            addElement(dropdown);
        }

        @Override
        public void onDropdownChanged(String elementId, int index, String value) {
            // Forward to the container model
            model.onDropdownChanged(elementId, index, value);
        }
    }
}
