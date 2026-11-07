package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTheme;
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

    private static final int TOOLBAR_GAP = 2;
    private static final int TOOLBAR_PADDING = 4;
    private static final int TOOLBAR_SPACING = 4;
    /** Fits the widest exporter label, "KJS" */
    private static final int TOOLBAR_BUTTON_WIDTH = 26;
    private static final int TITLE_TAB_HEIGHT = 21;

    public RecipeCreatorContainerScreenDefinition(ContainerModel<T> parent, T behavior, Identifier id, String title)
    {
        super(parent, behavior, id, title);
    }

    @Override
    public void initElements(int screenWidth, int screenHeight)
    {
        // Toolbar on the right of the GUI, so nothing covers the texture: exporter, then export
        addElement(recipeTypeDropdown = new CoreDropdown<>("recipe_type_dropdown", getToolbarX(), getGuiSize().getY(TOOLBAR_PADDING), TOOLBAR_BUTTON_WIDTH, 20, true, Arrays.stream(SupportedRecipeExporter.values()).toList(), 0,
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

        addElement(settingsList = new CoreButtonWidgetList("recipe_settings", getGuiSize().getX(-120), getGuiSize().getY(), 120, 100, Core2DBounds.ofSize(20, 20), Identifier.fromMod("textures/gui/widgets/settings.png"), "Recipe Settings"));
        settingsList.setVisible(hasRecipeSettings());
        addSettings();
    }
    
    protected abstract boolean hasRecipeSettings();

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
        // The export button is 20 wide: centered in the toolbar
        return Core2DBounds.ofPos(getToolbarX() + (TOOLBAR_BUTTON_WIDTH - 20) / 2, getGuiSize().getY(TOOLBAR_PADDING + 20 + TOOLBAR_SPACING));
    }

    private int getToolbarX()
    {
        return getGuiSize().getRight() + TOOLBAR_GAP + TOOLBAR_PADDING;
    }

    @Override
    protected void renderBehindTexture(RenderContext ctx)
    {
        // Toolbar panel, attached to the GUI's right side
        CoreScreenDefinition.renderTextureWithSize(ctx, GUI_TEXTURE, getGuiSize().getRight() + TOOLBAR_GAP, getGuiSize().getY(),
                                                   TOOLBAR_BUTTON_WIDTH + TOOLBAR_PADDING * 2, 40 + TOOLBAR_SPACING + TOOLBAR_PADDING * 2, false, false);
        if(showsTitleTab())
        {
            // Title tab, going under the GUI's top edge so it looks attached
            CoreScreenDefinition.renderTextureWithSize(ctx, GUI_TEXTURE, getGuiSize().getX(), getTitleTabY(), getTitleTabWidth(), TITLE_TAB_HEIGHT + 4, false, false);
        }
    }

    /**
     * Title on a tab above the GUI, with the item of the recipe creator.
     */
    @Override
    protected void renderTitle(RenderContext ctx)
    {
        if(!showsTitleTab()) return;
        Identifier icon = getTitleIcon();
        int x = getGuiSize().getX();
        int y = getTitleTabY();
        if(icon != null)
            getRenderAdapter().drawItem(ctx, new CoreItemStack(icon, 1), x + 4, y + 3, 1F);
        getRenderAdapter().drawText(ctx, translateToString(getTitle()), x + 6 + (icon == null ? 0 : 18), y + 7, CoreTheme.TEXT_TITLE);
    }

    /**
     * @return whether the title is drawn on a tab above the GUI; false when the GUI shows it itself
     */
    protected boolean showsTitleTab()
    {
        return true;
    }

    private int getTitleTabY()
    {
        return getGuiSize().getY() - TITLE_TAB_HEIGHT;
    }

    private int getTitleTabWidth()
    {
        return getRenderAdapter().getTextWidth(translateToString(getTitle())) + (getTitleIcon() == null ? 0 : 18) + 12;
    }

    /**
     * @return item shown next to the title, or null for none
     */
    protected Identifier getTitleIcon()
    {
        return null;
    }

    @Override
    protected void fillBehavior(T behavior)
    {
        behavior.setSerializationType(recipeTypeDropdown.getSelectedValue());
        behavior.hasSettingsOpen(settingsList.isOpen());
    }

    @Override
    protected void onDataUpdated(T behavior)
    {
        recipeTypeDropdown.setSelectedValue(behavior.getSerializationType());
        settingsList.setOpen(behavior.hasSettingsOpen());
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
