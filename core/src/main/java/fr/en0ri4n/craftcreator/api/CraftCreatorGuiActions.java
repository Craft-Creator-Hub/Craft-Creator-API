package fr.en0ri4n.craftcreator.api;

import fr.en0ri4n.craftcreator.api.ui.ClickContext;

public final class CraftCreatorGuiActions {

    private CraftCreatorGuiActions() {}

    public static ClickContext.ClickActionWithContext openRecipeEditor() {
        return ctx -> {
            // Pure logic: e.g., select recipe, set flags, prepare data
            // No Minecraft/FML imports here.
        };
    }

    public static ClickContext.ClickActionWithContext runDatapackExport() {
        return ctx -> {
            // Call core DatapackHelper, etc.
        };
    }
}