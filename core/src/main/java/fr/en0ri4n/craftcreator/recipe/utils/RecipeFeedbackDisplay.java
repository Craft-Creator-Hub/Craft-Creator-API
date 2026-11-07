package fr.en0ri4n.craftcreator.recipe.utils;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.mod.SupportedRecipeExporter;
import fr.en0ri4n.craftcreator.api.ui.elements.CoreNotifications;
import fr.en0ri4n.craftcreator.api.ui.screen.CoreScreenDefinition;
import fr.en0ri4n.craftcreator.api.ui.screen.RecipeCreatorContainerScreenDefinition;
import fr.en0ri4n.craftcreator.impl.model.screen.JsonPreviewScreen;
import fr.en0ri4n.craftcreator.utils.Feedback;

/**
 * Client side: shows the server's answer to a recipe request in the open Craft Creator screen.
 */
public final class RecipeFeedbackDisplay
{
    private RecipeFeedbackDisplay() {}

    /**
     * Open the json preview for a preview answer, otherwise show the message as a notification.
     *
     * @param screen the open Craft Creator screen, or null
     * @return false when there is no screen to show it in (the caller then puts it in the chat)
     */
    public static boolean show(CoreScreenDefinition<?> screen, RecipeRequestFeedback feedback)
    {
        if(screen == null) return false;

        if(feedback.getFeedback() == Feedback.JSON_PREVIEW && feedback.isSuccess())
        {
            if(!(screen instanceof RecipeCreatorContainerScreenDefinition<?> parent)) return false;
            CraftCreatorAPI.get().getPlatform().getUiAdapter().openScreen(new JsonPreviewScreen(parent, feedback.getRecipeJson(), feedback.getRecipeName()));
            return true;
        }

        screen.notify(message(feedback), feedback.isSuccess() ? CoreNotifications.Kind.SUCCESS : CoreNotifications.Kind.ERROR);
        return true;
    }

    /**
     * The translated message, with the recipe's name and exporter for messages that show them.
     */
    public static String message(RecipeRequestFeedback feedback)
    {
        String name = feedback.getRecipeName() != null ? feedback.getRecipeName() : "";
        String exporter = feedback.getRecipeType() == SupportedRecipeExporter.KUBE_JS ? "KubeJS" : "Datapack";
        return CraftCreatorAPI.translateToString(feedback.getFeedback().getMessageKey(), name, exporter);
    }
}
