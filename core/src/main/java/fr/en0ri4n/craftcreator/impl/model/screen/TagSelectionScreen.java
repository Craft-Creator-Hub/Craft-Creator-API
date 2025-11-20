package fr.en0ri4n.craftcreator.impl.model.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreContainerScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.ScreenData;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.api.ui.screen.WidgetRenderer;
import fr.en0ri4n.craftcreator.impl.blockentity.behaviors.TaggableSlotsBlockEntityBehavior;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TagSelectionScreen extends CoreScreenDefinition<TagSelectionScreen.TagSelectionScreenData>
{
    private final CoreContainerScreenDefinition<?> parent;
    private final CoreItemStack clickedStack;
    private final List<Identifier> availableTags;
    private CoreList tagListElement;

    public TagSelectionScreen(CoreContainerScreenDefinition<?> parent, CoreItemStack clickedStack)
    {
        super(Identifier.fromMod("tag_selection_screen"), "Tag Selection Screen",new TagSelectionScreenData()); // TODO: localization
        this.parent = parent;
        this.clickedStack = clickedStack;
        this.availableTags = new ArrayList<>();
    }

    @Override
    public void renderBackground(RenderContext ctx)
    {
        renderBackgroundWithSize(ctx, 176, 166);
    }

    @Override
    public void render(RenderContext ctx)
    {
        getCurrentRenderAdapter().drawItem(ctx, clickedStack, getGuiLeft() + 10, getGuiTop() + 10);
    }

    @Override
    public void init(WidgetRenderer renderer)
    {
        // List of tags
        addElement(tagListElement = new CoreList("tag_list",
                10,
                10,
                150,
                160,
                List.of("#minecraft:logs", "#minecraft:planks", "#minecraft:stones", "#minecraft:dirt", "#minecraft:sand"),
                4,
                "Select a tag"));

        super.init(renderer);
    }

    @Override
    public void updateScreen(UiUpdateData data)
    {
        super.updateScreen(data);
    }

    @Override
    public void fetchData()
    {
        this.availableTags.clear();
        this.availableTags.addAll(CraftCreatorAPI.get().getPlatform().getTagProvider().getTags(this.clickedStack));
        this.tagListElement.setEntries(this.availableTags.stream().map(Identifier::toString).toList());
    }

    @Override
    public void sendUpdates()
    {
        JsonObject payload = new JsonObject();
        getScreenData().getTaggableSlotsBlockEntityBehavior().save(null, payload);
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendDataUpdateToServer(
                new BlockEntityUpdateData(
                        this.parent.getParentContainerModel().getBlockEntityPos(),
                        this.parent.getId(),
                        payload
                )
        );
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
