package fr.en0ri4n.craftcreator.impl.model.screen;

import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.ScreenData;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreList;
import fr.en0ri4n.craftcreator.utils.Identifier;

import java.util.List;

public class TagSelectionScreen extends CoreScreenDefinition<TagSelectionScreen.TagSelectionScreenData>
{
    public TagSelectionScreen()
    {
        super(Identifier.fromMod("tag_selection_screen"), "Tag Selection Screen", new TagSelectionScreenData()); // TODO: localization
    }

    @Override
    public void init()
    {
        // List of tags
        addElement(new CoreList("tag_list",
                10,
                10,
                150,
                200,
                List.of("#minecraft:logs", "#minecraft:planks", "#minecraft:stones", "#minecraft:dirt", "#minecraft:sand"),
                4,
                "Select a tag"));
    }

    @Override
    public Identifier getBackgroundTexture()
    {
        return null;
    }

    public static class TagSelectionScreenData implements ScreenData
    {
        @Override
        public void load(com.google.gson.JsonObject payload)
        {
        }

        @Override
        public com.google.gson.JsonObject save()
        {
            return new com.google.gson.JsonObject();
        }
    }
}
