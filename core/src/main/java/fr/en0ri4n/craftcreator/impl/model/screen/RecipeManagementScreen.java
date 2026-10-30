package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.mod.SupportedMod;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementRequest;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementState;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.container.SlotDescriptor;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTextInput;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreTheme;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeManagementListener;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreator;
import fr.en0ri4n.craftcreator.recipe.creator.RecipeCreators;
import fr.en0ri4n.craftcreator.recipe.exporter.ModRecipeExporter;
import fr.en0ri4n.craftcreator.recipe.model.Recipe;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeEntry;
import fr.en0ri4n.craftcreator.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Browse recipes and manage the ones made with Craft Creator:
 * <ul>
 *     <li>All: every recipe of the game. Your recipes can be deleted, others removed with KubeJS</li>
 *     <li>Added: recipes added through the selected exporter, which can be deleted</li>
 *     <li>Removed: KubeJS removals, which can be cancelled</li>
 * </ul>
 * Changes are made by the server and applied on the next /reload: until then, changed recipes are shown in red.<br>
 * The layout is computed from the window size in {@link #initElements}, which runs again on every resize.
 */
public class RecipeManagementScreen extends CoreScreenDefinition<RecipeManagementScreen.RecipeManagementScreenData> implements RecipeManagementListener
{
    private static final int MESSAGE_DURATION_MS = 5000;
    private static final int MARGIN = 10;
    private static final int GAP = 4;
    private static final int BUTTON_HEIGHT = 18;
    private static final int TOP = 26;
    private static final int INFO_WIDTH = 110;
    private static final int PENDING_TINT = 0x50FF3030;

    private final Map<Tab, CoreButton> tabButtons = new EnumMap<>(Tab.class);
    private CoreList<ListedItem> listElement;
    private CoreDropdown<SupportedRecipeExporter> exporterDropdown;
    private CoreButton actionButton;
    private Runnable action = () -> {};

    /** Panel right of the list, and the area inside it where the selected entry is shown. */
    private Core2DBounds detailsPanel = Core2DBounds.ofSize(0, 0);
    private Core2DBounds detailsArea = Core2DBounds.ofSize(0, 0);
    private int bottomBarY;

    private Tab currentTab = Tab.ALL;
    private SupportedRecipeExporter exporter = SupportedRecipeExporter.MINECRAFT_DATAPACK;
    private List<Recipe> gameRecipes = List.of();
    /** Recipes added through each exporter; an exporter is missing until the server answered. */
    private final Map<SupportedRecipeExporter, List<Recipe>> addedRecipes = new EnumMap<>(SupportedRecipeExporter.class);
    private List<JsonObject> removals = List.of();
    private boolean removalSupported = SupportedMod.isKubeJSLoaded();
    private String search = "";

    private String message;
    private int messageColor;
    private long messageTime;

    public RecipeManagementScreen()
    {
        super(Identifier.fromMod("recipe_management_screen"), "screen.recipe_management_screen.title", new RecipeManagementScreenData(), Core2DBounds.ofSize(0, 0));
    }

    @Override
    protected void initElements(int screenWidth, int screenHeight)
    {
        // Full screen: the size follows the window
        getSize().setWidth(screenWidth);
        getSize().setHeight(screenHeight);
        tabButtons.clear();

        int leftWidth = Math.max(170, Math.min(280, screenWidth * 35 / 100));
        bottomBarY = screenHeight - MARGIN - BUTTON_HEIGHT;
        int rightX = MARGIN + leftWidth + MARGIN;
        int rightWidth = screenWidth - rightX - MARGIN;

        /* Left column: tabs, search, list, exporter */
        int tabWidth = (leftWidth - GAP * (Tab.values().length - 1)) / Tab.values().length;
        for(Tab tab : Tab.values())
        {
            CoreButton button = new CoreButton("tab_" + tab.name().toLowerCase(), MARGIN + tab.ordinal() * (tabWidth + GAP), TOP, tabWidth, BUTTON_HEIGHT, tab.label, () -> setTab(tab), tab.tooltip);
            tabButtons.put(tab, button);
            addElement(button);
        }

        int searchY = TOP + BUTTON_HEIGHT + GAP;
        CoreTextInput searchInput = new CoreTextInput("search", CoreTextInput.TextInputType.STRING, MARGIN, searchY, leftWidth, 16, "", search, "Search name, type or item...", 64, null);
        searchInput.setOnChange(this::search);
        addElement(searchInput);

        int listY = searchY + 16 + GAP;
        addElement(listElement = new CoreList<>(MARGIN, listY, leftWidth, bottomBarY - GAP - listY, 20, List.of()));
        listElement.setSelectionListener(selected -> updateAction());

        addElement(exporterDropdown = new CoreDropdown<>("exporter", MARGIN, bottomBarY, leftWidth, BUTTON_HEIGHT, true, List.of(SupportedRecipeExporter.values()), exporter.ordinal(), "Recipes added with", this::setExporter)
        {
            @Override
            public String getSelectedValueAsString()
            {
                return "Exporter: " + exporterName(getSelectedValue());
            }
        });

        /* Right side: shortcuts, details, action */
        int tutorialWidth = 60;
        int removeScreenWidth = 110;
        addElement(new CoreButton("open_remove_screen", screenWidth - MARGIN - removeScreenWidth, TOP, removeScreenWidth, BUTTON_HEIGHT, "Remove recipes...", this::openRemoveScreen, "Remove every recipe matching a filter (KubeJS)"));
        addElement(new CoreButton("open_tutorial", screenWidth - MARGIN - removeScreenWidth - GAP - tutorialWidth, TOP, tutorialWidth, BUTTON_HEIGHT, "Tutorial", () -> getCurrentUiAdapter().openScreen(new TutorialScreen(this)), null));

        int detailsY = TOP + BUTTON_HEIGHT + GAP;
        detailsPanel = Core2DBounds.of(rightX, detailsY, rightWidth, bottomBarY - GAP - detailsY);
        detailsArea = Core2DBounds.of(rightX + 8, detailsY + 6, rightWidth - 16, detailsPanel.getHeight() - 12);

        addElement(actionButton = new CoreButton("action", rightX, bottomBarY, 110, BUTTON_HEIGHT, "", () -> action.run(), null));

        int closeWidth = 60;
        addElement(new CoreButton("close", screenWidth - MARGIN - closeWidth, bottomBarY, closeWidth, BUTTON_HEIGHT, "Close", () -> getCurrentUiAdapter().closeScreen(), null));

        setTab(currentTab);
    }

    private static String exporterName(SupportedRecipeExporter exporter)
    {
        return exporter == SupportedRecipeExporter.KUBE_JS ? "KubeJS" : "Datapack";
    }

    @Override
    public void fetchData()
    {
        gameRecipes = CraftCreatorAPI.get().getPlatform().getGameDataProvider().getClientRecipes();
        for(SupportedRecipeExporter recipeExporter : SupportedRecipeExporter.values())
            sendRequest(RecipeManagementRequest.list(recipeExporter));
        refreshList();
    }

    /* ---------------- server exchange ---------------- */

    private void sendRequest(RecipeManagementRequest request)
    {
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendManagementRequestToServer(request);
    }

    @Override
    public void onManagementState(RecipeManagementState state)
    {
        addedRecipes.put(state.getExporter(), state.getAddedRecipes().stream().map(ModRecipeExporter::toRecipe).filter(Objects::nonNull).toList());

        if(state.getExporter() == SupportedRecipeExporter.KUBE_JS)
        {
            removals = state.getRemovals();
            removalSupported = state.isRemovalSupported();
        }

        if(state.getFeedback() != null)
            showMessage(translateToString(state.getFeedback().getMessageKey(), "") + (state.isSuccess() ? " - /reload to apply" : ""), state.isSuccess());

        refreshList();
    }

    private void showMessage(String text, boolean success)
    {
        message = text;
        messageColor = success ? CoreTheme.TEXT_SUCCESS : CoreTheme.TEXT_ERROR;
        messageTime = System.currentTimeMillis();
    }

    /* ---------------- recipe status ---------------- */

    /**
     * What Craft Creator knows about a recipe of the game.
     */
    private enum Status
    {
        /** Not made with Craft Creator: can be removed with KubeJS. */
        GAME,
        /** Added with Craft Creator: can be deleted. */
        OWN,
        /** Removed with KubeJS, until the next reload. */
        PENDING_REMOVAL,
        /** Deleted from its exporter, until the next reload. */
        PENDING_DELETION
    }

    private Status getStatus(Recipe recipe)
    {
        if(findRemoval(recipe) != null) return Status.PENDING_REMOVAL;
        if(!recipe.getId().getNamespace().equals(CCReferences.MOD_ID)) return Status.GAME;
        if(findOwner(recipe) != null) return Status.OWN;
        // Only sure it was deleted once every exporter answered
        return addedRecipes.size() == SupportedRecipeExporter.values().length ? Status.PENDING_DELETION : Status.OWN;
    }

    /**
     * @return the exporter holding this game recipe (game recipes made by Craft Creator are named "craftcreator:{name}"), or null
     */
    private SupportedRecipeExporter findOwner(Recipe gameRecipe)
    {
        for(Map.Entry<SupportedRecipeExporter, List<Recipe>> e : addedRecipes.entrySet())
            if(e.getValue().stream().anyMatch(added -> added.getName().equals(gameRecipe.getId().getPath())))
                return e.getKey();
        return null;
    }

    private Recipe findAdded(SupportedRecipeExporter recipeExporter, Recipe gameRecipe)
    {
        return addedRecipes.getOrDefault(recipeExporter, List.of()).stream().filter(added -> added.getName().equals(gameRecipe.getId().getPath())).findFirst().orElse(null);
    }

    /**
     * @return the first KubeJS removal matching this recipe, or null
     */
    private JsonObject findRemoval(Recipe recipe)
    {
        return removals.stream().filter(filter -> matches(filter, recipe)).findFirst().orElse(null);
    }

    /**
     * Same keys as KubeJS removal filters: every key present must match.
     */
    private static boolean matches(JsonObject filter, Recipe recipe)
    {
        for(Map.Entry<String, JsonElement> e : filter.entrySet())
        {
            String value = e.getValue().getAsString();
            boolean match = switch(e.getKey())
            {
                case "id" -> recipe.getId().toString().equals(value);
                case "type" -> recipe.getType().toString().equals(value);
                case "mod" -> recipe.getId().getNamespace().equals(value);
                case "output" -> recipe.getOutputs().stream().anyMatch(output -> output.getId().toString().equals(value));
                case "input" -> recipe.getInputs().stream().anyMatch(input -> acceptsInput(input, value));
                default -> false;
            };
            if(!match) return false;
        }
        return true;
    }

    private static boolean acceptsInput(RecipeEntry input, String value)
    {
        if(input.isTag()) return value.equals("#" + input.getId());
        return input.getId().toString().equals(value) || input.getAlternatives().stream().anyMatch(id -> id.toString().equals(value));
    }

    /* ---------------- actions ---------------- */

    private ListedItem getSelected()
    {
        return listElement.getSelected().map(CoreList.Entry::getValue).orElse(null);
    }

    /**
     * Set the action button for the selected entry.
     */
    private void updateAction()
    {
        if(actionButton == null) return;

        ListedItem selected = getSelected();
        actionButton.setVisible(selected != null);
        actionButton.setEnabled(true);
        if(selected == null) return;

        if(selected.removal() != null)
        {
            setAction("Cancel removal", () -> sendRequest(new RecipeManagementRequest(RecipeManagementRequest.Action.RESTORE, SupportedRecipeExporter.KUBE_JS, selected.removal())));
            return;
        }

        Recipe recipe = selected.recipe();
        if(currentTab == Tab.ADDED)
        {
            setAction("Delete", () -> delete(exporter, recipe));
            return;
        }

        switch(getStatus(recipe))
        {
            case GAME ->
            {
                setAction("Remove (KubeJS)", () -> removeWithKubeJs(recipe));
                actionButton.setEnabled(removalSupported);
            }
            case OWN ->
            {
                SupportedRecipeExporter owner = findOwner(recipe);
                Recipe added = owner == null ? null : findAdded(owner, recipe);
                setAction("Delete", () -> { if(added != null) delete(owner, added); });
                actionButton.setEnabled(added != null);
            }
            case PENDING_REMOVAL ->
            {
                JsonObject removal = findRemoval(recipe);
                setAction("Cancel removal", () -> sendRequest(new RecipeManagementRequest(RecipeManagementRequest.Action.RESTORE, SupportedRecipeExporter.KUBE_JS, removal)));
            }
            case PENDING_DELETION ->
            {
                setAction("Deleted", () -> {});
                actionButton.setEnabled(false);
            }
        }
    }

    private void setAction(String label, Runnable newAction)
    {
        actionButton.setLabel(label);
        action = newAction;
    }

    private void removeWithKubeJs(Recipe recipe)
    {
        JsonObject filter = new JsonObject();
        filter.addProperty("id", recipe.getId().toString());
        sendRequest(new RecipeManagementRequest(RecipeManagementRequest.Action.REMOVE, SupportedRecipeExporter.KUBE_JS, filter));
    }

    /**
     * @param added the recipe as read from the exporter (its id is the content hash)
     */
    private void delete(SupportedRecipeExporter recipeExporter, Recipe added)
    {
        JsonObject payload = new JsonObject();
        payload.addProperty("id", added.getId().getPath());
        sendRequest(new RecipeManagementRequest(RecipeManagementRequest.Action.DELETE_ADDED, recipeExporter, payload));
    }

    private void openRemoveScreen()
    {
        getCurrentUiAdapter().openScreen(new RemoveRecipeScreen(this));
    }

    private void setExporter(SupportedRecipeExporter newExporter)
    {
        if(newExporter == null || newExporter == exporter) return;

        exporter = newExporter;
        refreshList();
    }

    /* ---------------- list ---------------- */

    private void setTab(Tab tab)
    {
        currentTab = tab;
        tabButtons.forEach((t, button) -> button.setSelected(t == tab));
        exporterDropdown.setVisible(tab == Tab.ADDED);
        refreshList();
    }

    private void refreshList()
    {
        if(listElement == null) return;

        ListedItem previous = getSelected();
        List<CoreList.Entry<ListedItem>> entries = switch(currentTab)
        {
            case ALL -> gameRecipes.stream().<CoreList.Entry<ListedItem>>map(recipe -> new RecipeListEntry(recipe, getStatus(recipe))).toList();
            case ADDED -> addedRecipes.getOrDefault(exporter, List.of()).stream().<CoreList.Entry<ListedItem>>map(recipe -> new RecipeListEntry(recipe, Status.OWN)).toList();
            case REMOVED -> removals.stream().<CoreList.Entry<ListedItem>>map(RemovalListEntry::new).toList();
        };
        listElement.setEntries(entries);
        listElement.setEmptyText(switch(currentTab)
        {
            case ALL -> "No recipes";
            case ADDED -> addedRecipes.containsKey(exporter) ? "No recipe added yet" : "Loading...";
            case REMOVED -> removalSupported ? "No recipe removed" : "Removing recipes needs KubeJS";
        });
        applySearch();
        if(previous != null) listElement.setSelectedValue(previous);
        updateAction();
    }

    private void search(String text)
    {
        search = text.trim().toLowerCase();
        applySearch();
    }

    private void applySearch()
    {
        listElement.setFilter(search.isEmpty() ? null : entry -> entry.getValue().matches(search));
    }

    /* ---------------- rendering ---------------- */

    @Override
    protected void renderTitle(RenderContext ctx)
    {
        String title = translateToString(getTitle());
        getRenderAdapter().drawText(ctx, title, getGuiSize().getHorizontalCenter(getRenderAdapter().getTextWidth(title)), 9, CoreTheme.TEXT_TITLE);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        renderTextureWithBounds(ctx, GUI_TEXTURE, detailsPanel, false, false);

        // Count where the exporter dropdown would be, when it's hidden
        if(currentTab != Tab.ADDED)
        {
            int shown = listElement.getVisibleEntries().size();
            int total = listElement.getEntries().size();
            String count = shown == total ? total + " entries" : shown + " / " + total + " entries";
            getRenderAdapter().drawText(ctx, count, MARGIN + 2, bottomBarY + 6, CoreTheme.TEXT_MUTED);
        }

        if(message != null && System.currentTimeMillis() - messageTime < MESSAGE_DURATION_MS)
            getRenderAdapter().drawText(ctx, trim(message, detailsArea.getWidth()), detailsArea.getX(), detailsArea.getBottom() - 9, messageColor);

        ListedItem selected = getSelected();
        if(selected == null)
        {
            drawCentered(ctx, "Select an entry to see it here", detailsArea.getVerticalCenter(8), CoreTheme.TEXT_MUTED);
            return;
        }

        if(selected.removal() != null)
        {
            renderRemovalDetails(ctx, selected.removal());
            return;
        }

        Recipe recipe = selected.recipe();
        int y = detailsArea.getY(2);
        getRenderAdapter().drawText(ctx, trim(recipe.getName(), detailsArea.getWidth()), detailsArea.getX(), y, CoreTheme.TEXT_BRIGHT);
        getRenderAdapter().drawText(ctx, trim(recipe.getType().toString(), detailsArea.getWidth()), detailsArea.getX(), y + 11, CoreTheme.TEXT_MUTED);

        String statusText = currentTab == Tab.ALL ? switch(getStatus(recipe))
        {
            case PENDING_REMOVAL -> "Removed - /reload to apply";
            case PENDING_DELETION -> "Deleted - /reload to apply";
            case OWN -> "Made with Craft Creator";
            case GAME -> null;
        } : null;
        if(statusText != null)
        {
            int color = statusText.startsWith("Made") ? CoreTheme.ACCENT : CoreTheme.TEXT_ERROR;
            getRenderAdapter().drawText(ctx, statusText, detailsArea.getRight() - getRenderAdapter().getTextWidth(statusText), y, color);
        }

        RecipeCreator<?> creator = RecipeCreators.getRecipeCreator(recipe.getType());
        Preview preview = creator == null ? null : getPreview(creator, recipe);
        if(preview == null)
            drawCentered(ctx, "No recipe creator for this type", detailsArea.getVerticalCenter(8), CoreTheme.TEXT_MUTED);
        else
        {
            CoreContainerScreenDefinition<?> screenDef = creator.getContainerModel().getScreenDefinition();
            Core2DBounds texture = screenDef.getBackgroundTextureSize();

            getRenderAdapter().startScale(ctx, preview.scale(), preview.scale());
            getRenderAdapter().drawTexture(ctx, screenDef.getBackgroundTexture(),
                                           Math.round(preview.x() / preview.scale()), Math.round(preview.y() / preview.scale()), texture.getWidth(), texture.getHeight(),
                                           screenDef.getTextureFileSize(), screenDef.getTextureFileSize(), 0, 0, texture.getWidth(), texture.getHeight());
            getRenderAdapter().endScale(ctx);

            renderRecipeSlots(ctx, recipe, creator, preview, false, mouseX, mouseY);
        }

        renderParameters(ctx, recipe);
    }

    /**
     * Recipe values (cooking time, mana, energy...) in a column on the right of the preview.
     */
    private void renderParameters(RenderContext ctx, Recipe recipe)
    {
        List<RecipeInfos.RecipeParameter> parameters = recipe.getInfos().getParameters();
        if(parameters.isEmpty()) return;

        int x = detailsArea.getRight() - INFO_WIDTH;
        int y = detailsArea.getY(32);
        getRenderAdapter().drawText(ctx, "Parameters", x, y, CoreTheme.TEXT_TITLE);
        y += 14;

        for(RecipeInfos.RecipeParameter parameter : parameters)
        {
            getRenderAdapter().drawText(ctx, trim(humanize(parameter.getName()), INFO_WIDTH), x, y, CoreTheme.TEXT_MUTED);
            getRenderAdapter().drawText(ctx, trim(formatValue(parameter.getRawValue()), INFO_WIDTH - 6), x + 6, y + 10, CoreTheme.TEXT_BRIGHT);
            y += 24;
        }
    }

    /**
     * "cookingtime" -> "Cooking time", "processingTime" / "water_mod" -> "Processing time" / "Water mod".
     */
    private static String humanize(String key)
    {
        String spaced = key.equals("cookingtime") ? "cooking time" : key.replaceAll("([a-z])([A-Z])", "$1 $2").replace('_', ' ').toLowerCase();
        return spaced.isEmpty() ? spaced : Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    private static String formatValue(String value)
    {
        return value.endsWith(".0") ? value.substring(0, value.length() - 2) : value;
    }

    private void renderRemovalDetails(RenderContext ctx, JsonObject removal)
    {
        int y = detailsArea.getY(2);
        getRenderAdapter().drawText(ctx, "Removed recipes matching:", detailsArea.getX(), y, CoreTheme.TEXT_BRIGHT);
        y += 16;
        for(Map.Entry<String, JsonElement> e : removal.entrySet())
        {
            getRenderAdapter().drawText(ctx, e.getKey() + ":", detailsArea.getX(8), y, CoreTheme.TEXT_MUTED);
            getRenderAdapter().drawText(ctx, trim(e.getValue().getAsString(), detailsArea.getWidth() - 60), detailsArea.getX(56), y, CoreTheme.TEXT);
            y += 12;
        }

        long matching = gameRecipes.stream().filter(recipe -> matches(removal, recipe)).count();
        getRenderAdapter().drawText(ctx, matching + " recipe(s) currently match", detailsArea.getX(), y + 8, CoreTheme.TEXT_MUTED);
    }

    private void drawCentered(RenderContext ctx, String text, int y, int color)
    {
        getRenderAdapter().drawText(ctx, text, detailsArea.getHorizontalCenter(getRenderAdapter().getTextWidth(text)), y, color);
    }

    private static String trim(String text, int maxWidth)
    {
        if(getRenderAdapter().getTextWidth(text) <= maxWidth) return text;
        int end = text.length();
        while(end > 0 && getRenderAdapter().getTextWidth(text.substring(0, end) + "...") > maxWidth) end--;
        return text.substring(0, end) + "...";
    }

    @Override
    public void renderForeground(RenderContext ctx, int mouseX, int mouseY)
    {
        ListedItem selected = getSelected();
        RecipeCreator<?> creator = selected == null || selected.recipe() == null ? null : RecipeCreators.getRecipeCreator(selected.recipe().getType());
        if(creator != null)
            renderRecipeSlots(ctx, selected.recipe(), creator, getPreview(creator, selected.recipe()), true, mouseX, mouseY);

        super.renderForeground(ctx, mouseX, mouseY);
    }

    /**
     * Where and how big the creator's GUI is drawn: under the recipe name, left of the parameters if any,
     * shrunk if the window is too small.
     */
    private record Preview(int x, int y, float scale) {}

    private Preview getPreview(RecipeCreator<?> creator, Recipe recipe)
    {
        Core2DBounds size = creator.getContainerModel().getScreenDefinition().getBackgroundTextureSize();
        int areaY = detailsArea.getY(28);
        int areaHeight = detailsArea.getBottom() - 14 - areaY; // keep the last line for messages
        int areaWidth = detailsArea.getWidth() - (recipe.getInfos().getParameters().isEmpty() ? 0 : INFO_WIDTH + 8);

        float scale = Math.min(1F, Math.min((float) areaWidth / size.getWidth(), (float) areaHeight / size.getHeight()));
        int width = Math.round(size.getWidth() * scale);
        int height = Math.round(size.getHeight() * scale);
        return new Preview(detailsArea.getX() + (areaWidth - width) / 2, areaY + (areaHeight - height) / 2, scale);
    }

    private void renderRecipeSlots(RenderContext ctx, Recipe recipe, RecipeCreator<?> creator, Preview preview, boolean isForeground, int mouseX, int mouseY)
    {
        List<SlotDescriptor> slots = creator.getContainerModel().getLayout().getSlots();
        List<SlotDescriptor> inputSlots = slots.stream().filter(slot -> slot.getType() == SlotDescriptor.SlotType.RECIPE_CREATOR_INPUT).toList();
        List<SlotDescriptor> outputSlots = slots.stream().filter(slot -> slot.getType() == SlotDescriptor.SlotType.RECIPE_CREATOR_OUTPUT).toList();
        int slotSize = Math.round(16 * preview.scale());

        int inputIndex = 0;
        int outputIndex = 0;
        for(RecipeEntry entry : recipe.getEntries())
        {
            boolean output = entry.getType() == RecipeEntry.EntryType.OUTPUT;
            SlotDescriptor slot = findSlot(entry, output ? outputSlots : inputSlots, output ? outputIndex++ : inputIndex++);
            if(slot == null) continue;

            List<Identifier> possibilities = getPossibleItems(entry);
            if(possibilities.isEmpty()) continue;
            Identifier itemId = possibilities.get((int) ((System.currentTimeMillis() / 1000) % possibilities.size()));

            int slotX = preview.x() + Math.round(slot.getX() * preview.scale());
            int slotY = preview.y() + Math.round(slot.getY() * preview.scale());
            CoreItemStack itemStack = new CoreItemStack(itemId, Math.max(1, entry.getCount()));

            if(!isForeground)
                getRenderAdapter().drawItem(ctx, itemStack, slotX, slotY, preview.scale());
            else if(mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize)
                getRenderAdapter().drawItemTooltip(ctx, itemStack, getExtraTooltip(entry, possibilities), mouseX, mouseY);
        }
    }

    private static List<String> getExtraTooltip(RecipeEntry entry, List<Identifier> possibilities)
    {
        if(entry.isTag()) return List.of("§8#" + entry.getId(), "§8" + possibilities.size() + " possible items");
        if(possibilities.size() > 1) return List.of("§8One of " + possibilities.size() + " items");
        return List.of();
    }

    /**
     * The entry's own slot when it matches the layout, otherwise the n-th slot of the right kind.
     */
    private static SlotDescriptor findSlot(RecipeEntry entry, List<SlotDescriptor> candidates, int fallbackIndex)
    {
        for(SlotDescriptor slot : candidates)
            if(slot.getIndex() == entry.getSlot()) return slot;
        return fallbackIndex < candidates.size() ? candidates.get(fallbackIndex) : null;
    }

    /**
     * Every item the entry accepts: the tag's items, the listed alternatives, or the item itself. Shown in turn, one per second.
     */
    private static List<Identifier> getPossibleItems(RecipeEntry entry)
    {
        if(entry.isTag())
            return CraftCreatorAPI.get().getPlatform().getTagProvider().getItemsInTag(entry.getId()).stream().map(CoreItemStack::getItemId).toList();
        if(!entry.getAlternatives().isEmpty()) return entry.getAlternatives();
        return List.of(entry.getId());
    }

    /* ---------------- list entries ---------------- */

    /**
     * A row of the list: either a recipe or a KubeJS removal filter.
     */
    public record ListedItem(Recipe recipe, JsonObject removal)
    {
        boolean matches(String search)
        {
            if(removal != null) return removal.toString().toLowerCase().contains(search);
            return recipe.getName().toLowerCase().contains(search)
                    || recipe.getType().toString().contains(search)
                    || recipe.getEntries().stream().anyMatch(e -> e.getId().toString().contains(search));
        }
    }

    private static class RecipeListEntry extends CoreList.Entry<ListedItem>
    {
        private final Status status;

        private RecipeListEntry(Recipe recipe, Status status)
        {
            super(recipe.getName(), new ListedItem(recipe, null), getIcon(recipe), null);
            this.status = status;
        }

        private boolean isPending()
        {
            return status == Status.PENDING_REMOVAL || status == Status.PENDING_DELETION;
        }

        @Override
        protected int getTint()
        {
            return isPending() ? PENDING_TINT : 0;
        }

        @Override
        protected int getTextColor(boolean selected, boolean hovered)
        {
            if(isPending()) return CoreTheme.TEXT_ERROR;
            if(status == Status.OWN && !selected) return CoreTheme.ACCENT;
            return super.getTextColor(selected, hovered);
        }

        /**
         * The first output, or the first input for recipes without item output (e.g. Botania brews).
         */
        private static Identifier getIcon(Recipe recipe)
        {
            if(!recipe.getOutputs().isEmpty()) return recipe.getOutputs().get(0).getId();
            if(!recipe.getInputs().isEmpty() && !recipe.getInputs().get(0).isTag()) return recipe.getInputs().get(0).getId();
            return null;
        }

        @Override
        public List<String> getTooltip(int width)
        {
            Recipe recipe = getValue().recipe();
            List<String> tooltips = new ArrayList<>();
            tooltips.add("§f" + recipe.getName());
            tooltips.add("§7" + recipe.getType());
            if(status == Status.PENDING_REMOVAL) tooltips.add("§cRemoved, /reload to apply");
            if(status == Status.PENDING_DELETION) tooltips.add("§cDeleted, /reload to apply");
            recipe.getInputs().forEach(input -> tooltips.add("§6- " + (input.isTag() ? "#" : "") + input.getId()));
            recipe.getOutputs().forEach(output -> tooltips.add("§b> " + output.getId()));
            return tooltips;
        }
    }

    private static class RemovalListEntry extends CoreList.Entry<ListedItem>
    {
        private RemovalListEntry(JsonObject filter)
        {
            super(describe(filter), new ListedItem(null, filter), null);
        }

        private static String describe(JsonObject filter)
        {
            List<String> parts = new ArrayList<>();
            filter.entrySet().forEach(e -> parts.add(e.getKey() + ": " + e.getValue().getAsString()));
            return String.join(", ", parts);
        }
    }

    private enum Tab
    {
        ALL("All", "Every recipe of the game"),
        ADDED("Added", "Recipes added with Craft Creator"),
        REMOVED("Removed", "Recipes removed with KubeJS");

        private final String label;
        private final String tooltip;

        Tab(String label, String tooltip)
        {
            this.label = label;
            this.tooltip = tooltip;
        }
    }

    public static class RecipeManagementScreenData implements ScreenData
    {
        @Override
        public void load(JsonObject jsonObject) {}

        @Override
        public JsonObject save() { return new JsonObject(); }
    }
}
