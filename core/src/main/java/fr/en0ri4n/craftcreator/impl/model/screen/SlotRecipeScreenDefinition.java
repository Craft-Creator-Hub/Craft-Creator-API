package fr.en0ri4n.craftcreator.impl.model.screen;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButtonStyle;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTextInput;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTheme;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTooltip;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreUiElement;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.SlotRecipeBehavior;
import fr.en0ri4n.craftcreator.impl.model.container.SlotRecipeContainerModel;
import fr.en0ri4n.craftcreator.recipe.slot.ParamSpec;
import fr.en0ri4n.craftcreator.recipe.slot.SlotRecipeType;
import fr.en0ri4n.craftcreator.recipe.slot.SlotSpec;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Screen of a {@link SlotRecipeType}: background and slots come from the type, its values are edited in the settings panel,
 * and a button switches between its variants when it has several.<br>
 * The type's decorations (arrows, machines, labels) are drawn over the texture, and hovering an empty slot tells what goes in it.
 */
public class SlotRecipeScreenDefinition extends RecipeCreatorContainerScreenDefinition<SlotRecipeBehavior>
{
    private static final int ARROW_COLOR = 0xFFE3B553;
    private static final int ARROW_SHADOW = 0xFF5A3E16;
    private static final int ARROW_HEAD = 4;

    private final SlotRecipeType type;
    private final Map<String, ParamWidget> paramWidgets = new HashMap<>();
    private CoreDropdown<SlotRecipeType.Variant> variantDropdown;

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
    protected Identifier getTitleIcon()
    {
        return type.getIcon();
    }

    @Override
    protected boolean showsTitleTab()
    {
        return type.getTitlePosition() == null;
    }

    @Override
    public void initElements(int screenWidth, int screenHeight)
    {
        paramWidgets.clear();
        variantDropdown = null;
        super.initElements(screenWidth, screenHeight);

        SlotRecipeType.VariantSelector selector = type.getVariantSelector();
        if(selector != null)
            addElement(variantDropdown = createVariantDropdown(selector));
    }

    private CoreDropdown<SlotRecipeType.Variant> createVariantDropdown(SlotRecipeType.VariantSelector selector)
    {
        int index = type.getVariants().indexOf(getBehavior().getVariant());
        return new CoreDropdown<>("variant", getGuiSize().getX(selector.x()), getGuiSize().getY(selector.y()), selector.width(), selector.height(), true,
                                  type.getVariants(), index, "Recipe type", this::setVariant)
        {
            @Override
            public String getSelectedValueAsString()
            {
                SlotRecipeType.Variant variant = getSelectedValue();
                return variant == null ? "" : variant.label();
            }

            @Override
            public void render(RenderContext ctx, int mouseX, int mouseY, float partialTick)
            {
                if(!selector.showIcon())
                {
                    super.render(ctx, mouseX, mouseY, partialTick);
                    return;
                }
                // Icon style: the variant's item instead of its label
                if(!isVisible() || getSelectedValue() == null) return;
                CoreButtonStyle.State state = CoreButtonStyle.State.of(true, false, isMouseOver(mouseX, mouseY));
                CoreButtonStyle.draw(ctx, getBounds(), state);
                getRenderAdapter().drawItem(ctx, new CoreItemStack(getSelectedValue().icon(), 1),
                                            getBounds().getHorizontalCenter(16), CoreButtonStyle.textY(getBounds(), 16, state), 1F);
            }
        };
    }

    private void setVariant(SlotRecipeType.Variant variant)
    {
        if(variant == null || variant == getBehavior().getVariant()) return;
        getBehavior().setVariant(variant.name());
        sendUpdates();
    }

    /* ---------------- settings ---------------- */

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
            String label = translateToString(param.label());
            ParamWidget widget = switch(param.type())
            {
                case INTEGER -> textWidget(param, CoreTextInput.TextInputType.INTEGER, 40, label);
                case FLOAT -> textWidget(param, CoreTextInput.TextInputType.FLOAT, 40, label);
                case STRING -> textWidget(param, CoreTextInput.TextInputType.STRING, 80, label);
                case CHOICE, BOOLEAN -> choiceWidget(param, label);
            };
            paramWidgets.put(param.key(), widget);
            addRecipeSetting(label, widget.element());
        }
    }

    private ParamWidget textWidget(ParamSpec param, CoreTextInput.TextInputType inputType, int width, String label)
    {
        CoreTextInput input = new CoreTextInput(param.key(), inputType, 0, 0, width, 12, "", param.defaultValue(), param.defaultValue(), 64, label);
        input.setOnChange(value -> getBehavior().setParam(param.key(), value));
        return new ParamWidget(input, input::getValue, input::setValue);
    }

    /**
     * A button cycling through the values, showing them as "Yes"/"No" or capitalized words.
     */
    private ParamWidget choiceWidget(ParamSpec param, String label)
    {
        List<String> options = param.options();
        CoreDropdown<String> dropdown = new CoreDropdown<>(param.key(), 0, 0, 70, 14, true, options, Math.max(0, options.indexOf(param.defaultValue())), label,
                                                           value -> { if(value != null) getBehavior().setParam(param.key(), value); })
        {
            @Override
            public String getSelectedValueAsString()
            {
                return displayValue(param, getSelectedValue());
            }
        };
        Consumer<String> setter = value -> { if(options.contains(value)) dropdown.setSelectedValue(value); };
        return new ParamWidget(dropdown, dropdown::getSelectedValue, setter);
    }

    private static String displayValue(ParamSpec param, String value)
    {
        if(value == null || value.isEmpty()) return "";
        if(param.type() == ParamSpec.Type.BOOLEAN) return Boolean.parseBoolean(value) ? "Yes" : "No";
        return Character.toUpperCase(value.charAt(0)) + value.substring(1).replace('_', ' ');
    }

    @Override
    protected void fillBehavior(SlotRecipeBehavior behavior)
    {
        super.fillBehavior(behavior);
        if(variantDropdown != null && variantDropdown.getSelectedValue() != null)
            behavior.setVariant(variantDropdown.getSelectedValue().name());
        paramWidgets.forEach((key, widget) ->
        {
            String value = widget.getter().get();
            if(value != null) behavior.setParam(key, value);
        });
    }

    @Override
    protected void onDataUpdated(SlotRecipeBehavior behavior)
    {
        super.onDataUpdated(behavior);
        if(variantDropdown != null)
            variantDropdown.setSelectedValue(behavior.getVariant());
        paramWidgets.forEach((key, widget) ->
        {
            String value = behavior.getParams().get(key);
            if(value != null) widget.setter().accept(value);
        });
    }

    /* ---------------- rendering ---------------- */

    @Override
    public void renderBackground(RenderContext ctx)
    {
        super.renderBackground(ctx);
        for(SlotRecipeType.Decoration decoration : type.getDecorations())
        {
            // Items are drawn over the slots, in render()
            if(decoration instanceof SlotRecipeType.Arrow arrow)
                drawArrow(ctx, getGuiSize().getX(arrow.x()), getGuiSize().getY(arrow.y()), arrow.length(), arrow.down());
            else if(decoration instanceof SlotRecipeType.Label label)
                getRenderAdapter().drawText(ctx, translateToString(label.text()), getGuiSize().getX(label.x()), getGuiSize().getY(label.y()), label.color());
        }
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        super.render(ctx, mouseX, mouseY);
        for(SlotRecipeType.Decoration decoration : type.getDecorations())
        {
            Identifier item = getDecorationItem(decoration);
            if(item != null)
                getRenderAdapter().drawItem(ctx, new CoreItemStack(item, 1), getGuiSize().getX(decorationX(decoration)), getGuiSize().getY(decorationY(decoration)), 1F);
        }

        if(type.getTitlePosition() != null)
        {
            int[] position = type.getTitlePosition();
            getRenderAdapter().drawText(ctx, translateToString(getTitle()), getGuiSize().getX(position[0]), getGuiSize().getY(position[1]), CoreTheme.TEXT_TITLE);
        }
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        super.renderForeground(ctx, mouseX, mouseY);
        if(getRenderAdapter().getItemStackUnderMouse(mouseX, mouseY) != null) return;

        // Machine decorations name their item
        for(SlotRecipeType.Decoration decoration : type.getDecorations())
        {
            Identifier item = getDecorationItem(decoration);
            if(item != null && isOver(mouseX, mouseY, decorationX(decoration), decorationY(decoration)))
            {
                CoreTooltip.create().title(getRenderAdapter().getItemName(new CoreItemStack(item, 1)), item).render(ctx, mouseX, mouseY);
                return;
            }
        }

        // Empty recipe slots tell what goes in them
        List<SlotSpec> slots = type.getSlots();
        for(SlotSpec slot : slots)
        {
            if(!isOver(mouseX, mouseY, slot.x(), slot.y())) continue;
            CoreTooltip tooltip = CoreTooltip.create().title(slotName(slot)).subtitle(slot.output() ? "Output" : "Input");
            switch(slot.kind())
            {
                case FLUID -> tooltip.text("Place a bucket or any item holding the fluid", CoreTheme.TEXT_MUTED);
                case BLOCK -> tooltip.text("Place the block's item", CoreTheme.TEXT_MUTED);
                default -> { if(!slot.output()) tooltip.text("Ctrl + click with an item to use a tag", CoreTheme.TEXT_MUTED); }
            }
            tooltip.render(ctx, mouseX, mouseY);
            return;
        }
    }

    private boolean isOver(int mouseX, int mouseY, int x, int y)
    {
        int left = getGuiSize().getX(x);
        int top = getGuiSize().getY(y);
        return mouseX >= left && mouseX < left + 16 && mouseY >= top && mouseY < top + 16;
    }

    private Identifier getDecorationItem(SlotRecipeType.Decoration decoration)
    {
        if(decoration instanceof SlotRecipeType.Item item) return item.itemId();
        if(decoration instanceof SlotRecipeType.ParamIcon icon) return icon.icons().get(currentParam(icon.paramKey()));
        return null;
    }

    private static int decorationX(SlotRecipeType.Decoration decoration)
    {
        return decoration instanceof SlotRecipeType.Item item ? item.x() : decoration instanceof SlotRecipeType.ParamIcon icon ? icon.x() : 0;
    }

    private static int decorationY(SlotRecipeType.Decoration decoration)
    {
        return decoration instanceof SlotRecipeType.Item item ? item.y() : decoration instanceof SlotRecipeType.ParamIcon icon ? icon.y() : 0;
    }

    private String currentParam(String key)
    {
        String value = getBehavior().getParams().get(key);
        return value != null ? value : type.getParam(key).map(ParamSpec::defaultValue).orElse("");
    }

    /**
     * "fluid_in_0" -> "Fluid in 1": the slot's name, with numbers starting at 1.
     */
    private static String slotName(SlotSpec slot)
    {
        String[] words = slot.name().split("_");
        StringBuilder name = new StringBuilder();
        for(String word : words)
        {
            if(!name.isEmpty()) name.append(' ');
            name.append(word.chars().allMatch(Character::isDigit) ? String.valueOf(Integer.parseInt(word) + 1) : word);
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * A thick arrow with an outline, pointing right or down from (x, y): 9 pixels wide across.
     */
    private void drawArrow(RenderContext ctx, int x, int y, int length, boolean down)
    {
        int shaft = length - ARROW_HEAD - 1;
        if(down)
        {
            getRenderAdapter().drawRect(ctx, x + 2, y, 5, shaft, ARROW_SHADOW);
            getRenderAdapter().drawRect(ctx, x + 3, y, 3, shaft, ARROW_COLOR);
            for(int row = 0; row <= ARROW_HEAD; row++)
            {
                int half = ARROW_HEAD - row;
                getRenderAdapter().drawRect(ctx, x + 4 - half - 1, y + shaft + row, half * 2 + 3, 1, ARROW_SHADOW);
                getRenderAdapter().drawRect(ctx, x + 4 - half, y + shaft + row, half * 2 + 1, 1, ARROW_COLOR);
            }
            return;
        }
        getRenderAdapter().drawRect(ctx, x, y + 2, shaft, 5, ARROW_SHADOW);
        getRenderAdapter().drawRect(ctx, x, y + 3, shaft, 3, ARROW_COLOR);
        for(int col = 0; col <= ARROW_HEAD; col++)
        {
            int half = ARROW_HEAD - col;
            getRenderAdapter().drawRect(ctx, x + shaft + col, y + 4 - half - 1, 1, half * 2 + 3, ARROW_SHADOW);
            getRenderAdapter().drawRect(ctx, x + shaft + col, y + 4 - half, 1, half * 2 + 1, ARROW_COLOR);
        }
    }

    /**
     * A settings widget with a way to read and set its value as text.
     */
    private record ParamWidget(CoreUiElement element, java.util.function.Supplier<String> getter, Consumer<String> setter) {}
}
