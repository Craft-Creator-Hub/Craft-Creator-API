package fr.en0ri4n.craftcreator.init;

import fr.en0ri4n.craftcreator.impl.item.CCItem;

import java.util.ArrayList;
import java.util.List;

public abstract class InitItemBase
{
    protected final List<CCItem> items = new ArrayList<>();

    protected InitItemBase()
    {

    }

    public abstract void registerItems();
}
