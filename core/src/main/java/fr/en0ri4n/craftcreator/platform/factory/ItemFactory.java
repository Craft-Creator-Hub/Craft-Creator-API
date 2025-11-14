package fr.en0ri4n.craftcreator.platform.factory;

import fr.en0ri4n.craftcreator.impl.item.CCItem;

public interface ItemFactory<Item>
{
    Item createItem(CCItem ccItem);
}
