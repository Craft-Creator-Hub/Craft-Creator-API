package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.api.ui.screen.*;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.TaggableSlotsBlockEntityBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TagSelectionScreen extends CoreScreenDefinition<TagSelectionScreen.TagSelectionScreenData>
{
    private final TaggableSlotsContainerScreenDefinition<?> parent;
    private final CoreItemStack clickedStack;
    private final List<Identifier> availableTags;
    private final Integer clickedIndex;
    private CoreList tagListElement;

    public TagSelectionScreen(TaggableSlotsContainerScreenDefinition<?> parent, Pair<Integer, CoreItemStack> clickedStack)
    {
        super(Identifier.fromMod("tag_selection_screen"), "Tag Selection Screen", new TagSelectionScreenData()); // TODO: localization
        getScreenData().load(parent.getScreenData().save());
        this.parent = parent;
        this.clickedStack = clickedStack.getSecond();
        this.clickedIndex = clickedStack.getFirst();
        this.availableTags = new ArrayList<>();
    }

    @Override
    public void renderBackground(RenderContext ctx)
    {
        super.renderBackground(ctx);
        renderTextureWithSize(ctx, GUI_TEXTURE, getGuiLeft(), getGuiTop(), 176, 166, false, false);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        float scale = 2F;
        int itemWidth = Math.round(16 * scale);
        getCurrentRenderAdapter().drawItem(ctx, clickedStack, getGuiLeft() + (getGuiSize().getFirst() - itemWidth) / 2, getGuiTop() + getGuiSize().getSecond() - 36, scale);
    }

    @Override
    public void init(WidgetRenderer renderer)
    {
        cleanScreen();
        // List of tags
        addElement(tagListElement = new CoreList(getGuiLeft() + 10, getGuiTop() + 10, getGuiSize().getFirst() - 20, getGuiSize().getSecond() - 50, 12, List.of()));
        addElement(new CoreButton("back_button", getGuiLeft() + 10, getGuiTop() + 135, 50, 20, "Back", this::goBack, "Return to the previous screen"));
        addElement(new CoreButton("apply_button", getGuiLeft() + 115, getGuiTop() + 135, 50, 20, "Apply", this::apply, "Apply selected tag to the item"));

        super.init(renderer);
    }

    private void apply()
    {
        if(tagListElement.getSelected().isPresent())
            getScreenData().getTaggableSlotsBlockEntityBehavior().getTaggedSlots().put(clickedIndex, Identifier.from(tagListElement.getSelected().get().getValue()));

        sendUpdates();
        goBack();
    }

    private void goBack()
    {
        OpenContainerRequestData request = new OpenContainerRequestData(this.parent.getParentContainerModel().getBlockEntityPos(), this.parent.getId());
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendOpenContainerRequestToServer(request);
    }

    @Override
    public void fetchData()
    {
        this.availableTags.clear();
        this.availableTags.addAll(CraftCreatorAPI.get().getPlatform().getTagProvider().getTags(this.clickedStack));
        this.tagListElement.setEntries(this.availableTags.stream().map(id -> new CoreList.Entry(id.toString(), id.toString(), null)).toList());
    }

    @Override
    public void sendUpdates()
    {
        JsonObject payload = new JsonObject();
        getScreenData().getTaggableSlotsBlockEntityBehavior().save(null, payload);
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendDataUpdateToServer(new BlockEntityUpdateData(this.parent.getParentContainerModel().getBlockEntityPos(), this.parent.getId(), payload));
    }

    @Getter
    public static class TagSelectionScreenData implements ScreenData
    {
        private final TaggableSlotsBlockEntityBehavior taggableSlotsBlockEntityBehavior;

        protected TagSelectionScreenData()
        {
            this.taggableSlotsBlockEntityBehavior = new TaggableSlotsBlockEntityBehavior();
        }

        @Override
        public void load(JsonObject payload)
        {
            this.taggableSlotsBlockEntityBehavior.load(null, payload);
        }

        @Override
        public JsonObject save()
        {
            JsonObject obj = new JsonObject();
            this.taggableSlotsBlockEntityBehavior.save(null, obj);
            return obj;
        }
    }
}
