package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
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
    private CoreList<String> tagListElement;

    public TagSelectionScreen(TaggableSlotsContainerScreenDefinition<?> parent, Pair<Integer, CoreItemStack> clickedStack)
    {
        super(Identifier.fromMod("tag_selection_screen"),
              translateToString("screen.tag_selector.title"),
              new TagSelectionScreenData(),
              Core2DBounds.ofSize(176 * 2, 166));
        this.parent = parent;
        this.clickedStack = clickedStack.getSecond();
        this.clickedIndex = clickedStack.getFirst();
        this.availableTags = new ArrayList<>();
    }

    @Override
    public void renderBackground(RenderContext ctx)
    {
        parent.render(ctx, 0, 0);
        getRenderAdapter().drawRect(ctx, 0, 0, getRenderAdapter().getScreenWidth(), getRenderAdapter().getScreenHeight(), 0xD0101010);
        renderTextureWithSize(ctx, GUI_TEXTURE, getGuiSize().getX(), getGuiSize().getY(), getGuiSize().getWidth(), getGuiSize().getHeight(), false, false);
    }

    @Override
    public void render(RenderContext ctx, int mouseX, int mouseY)
    {
        float scale = 1.6F;
        int itemSize = Math.round(16 * scale);
        getRenderAdapter().drawItem(ctx, clickedStack, getGuiSize().getHorizontalCenter(itemSize), getGuiSize().getBottom(-itemSize - 6), scale);
    }

    @Override
    public void initElements(int screenWidth, int screenHeight)
    {
        getScreenData().load(parent.getScreenData().save()); // Load existing data from parent

        // List of tags
        addElement(tagListElement = new CoreList<>(getGuiSize().getX(10), getGuiSize().getY(10), getGuiSize().getWidth(-20), getGuiSize().getHeight(-50), 14, List.of()));
        addElement(new CoreButton("back_button", getGuiSize().getX(10), getGuiSize().getY(135), 50, 20, "Back", this::goBack, "Return to the previous screen"));
        addElement(new CoreButton("apply_button", getGuiSize().getRight(-60), getGuiSize().getY(135), 50, 20, "Apply", this::apply, "Apply selected tag to the item"));
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
        this.tagListElement.setEntries(this.availableTags.stream().map(id -> new CoreList.Entry<>(id.toString(), id.toString(), null)).toList());
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
