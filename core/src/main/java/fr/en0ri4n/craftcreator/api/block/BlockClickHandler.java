package fr.en0ri4n.craftcreator.api.block;

import fr.en0ri4n.craftcreator.api.ui.ClickContext;

@FunctionalInterface
public interface BlockClickHandler {
    void onClick(CCBlock block, ClickContext ctx);
}