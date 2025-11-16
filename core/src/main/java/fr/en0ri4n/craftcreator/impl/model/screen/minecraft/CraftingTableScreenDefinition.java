package fr.en0ri4n.craftcreator.impl.model.screen.minecraft;

import fr.en0ri4n.craftcreator.api.ui.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreButton;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreDropdown;
import fr.en0ri4n.craftcreator.utils.Identifier;
import fr.en0ri4n.craftcreator.utils.Pair;

import java.util.List;

public class CraftingTableScreenDefinition extends CoreScreenDefinition
{
    public CraftingTableScreenDefinition()
    {
        super("crafting_table_recipe_creator", "Crafting Table Recipe Creator"); // TODO: localization
    }

    @Override
    public Identifier getBackgroundTexture()
    {
        return Identifier.fromMod("textures/gui/container/minecraft/crafting_recipe_creator.png");
    }

    @Override
    public Pair<Integer, Integer> getBackgroundTextureSize()
    {
        return Pair.create(176, 166);
    }

    @Override
    public void init()
    {
        addElement(new CoreDropdown("mod_selector", 10, 10, 100, 20, List.of("Minecraft", "Create", "Botania", "Thermal"), -1, ""));
        addElement(new CoreButton("export_button", 10, 40, 80, 20, "Export", "export_recipes", "Export the currently selected recipes"));
    }
}
