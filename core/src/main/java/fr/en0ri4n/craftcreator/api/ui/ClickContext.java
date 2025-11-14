package fr.en0ri4n.craftcreator.api.ui;

import fr.en0ri4n.craftcreator.utils.Identifier;

public interface ClickContext {
    Identifier getTargetBlockId();

    @FunctionalInterface
    interface ClickActionWithContext {
        void execute(ClickContext ctx);
    }
}