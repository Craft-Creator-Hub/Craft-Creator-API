package fr.en0ri4n.craftcreator.impl.model.screen;

import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTextInput;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.SlotRecipeBehavior;
import fr.en0ri4n.craftcreator.impl.model.container.SlotRecipeContainerModel;
import fr.en0ri4n.craftcreator.recipe.slot.ParamSpec;
import fr.en0ri4n.craftcreator.recipe.slot.SlotRecipeType;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Screen of a {@link SlotRecipeType}: background and slots come from the type, its values are edited in the settings panel.
 */
public class SlotRecipeScreenDefinition extends RecipeCreatorContainerScreenDefinition<SlotRecipeBehavior>
{
    private final SlotRecipeType type;
    private final Map<String, CoreTextInput> paramInputs = new HashMap<>();

    public SlotRecipeScreenDefinition(SlotRecipeContainerModel parent)
    {
        super(parent, new SlotRecipeBehavior(parent.getType()), parent.getType().getId(), parent.getType().getTitle());
        this.type = parent.getType();
    }

    @Override
    public Identifier getBackgroundTexture()
    {
        return type.getTexture();
    }

    @Override
    public int getTextureFileSize()
    {
        return type.getTextureSize();
    }

    @Override
    public Core2DBounds getBackgroundTextureSize()
    {
        return Core2DBounds.ofSize(type.getGuiWidth(), type.getGuiHeight());
    }

    @Override
    public void initElements(int screenWidth, int screenHeight)
    {
        paramInputs.clear();
        super.initElements(screenWidth, screenHeight);
    }

    @Override
    protected boolean hasRecipeSettings()
    {
        return !type.getParams().isEmpty();
    }

    @Override
    protected void addSettings()
    {
        for(ParamSpec param : type.getParams())
        {
            CoreTextInput.TextInputType inputType = switch(param.type())
            {
                case INTEGER -> CoreTextInput.TextInputType.INTEGER;
                case FLOAT -> CoreTextInput.TextInputType.FLOAT;
                case STRING -> CoreTextInput.TextInputType.STRING;
            };
            int width = param.type() == ParamSpec.Type.STRING ? 80 : 40;

            CoreTextInput input = new CoreTextInput(param.key(), inputType, 0, 0, width, 12, "", param.defaultValue(), param.defaultValue(), 64, translateToString(param.label()));
            input.setOnChange(value -> getBehavior().setParam(param.key(), value));
            paramInputs.put(param.key(), input);
            addRecipeSetting(translateToString(param.label()), input);
        }
    }

    @Override
    protected void fillBehavior(SlotRecipeBehavior behavior)
    {
        super.fillBehavior(behavior);
        paramInputs.forEach((key, input) -> behavior.setParam(key, input.getValue()));
    }

    @Override
    protected void onDataUpdated(SlotRecipeBehavior behavior)
    {
        super.onDataUpdated(behavior);
        paramInputs.forEach((key, input) ->
        {
            String value = behavior.getParams().get(key);
            if(value != null) input.setValue(value);
        });
    }
}
