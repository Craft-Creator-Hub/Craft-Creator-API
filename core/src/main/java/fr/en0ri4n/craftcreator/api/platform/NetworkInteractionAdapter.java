package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.recipe.utils.RecipeRequestFeedback;

import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementRequest;
import fr.en0ri4n.craftcreator.api.net.RecipeManagementState;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;

public interface NetworkInteractionAdapter<T>
{
    void sendDataUpdateToClient(T player, UiUpdateData data);

    void sendDataUpdateToServer(BlockEntityUpdateData data);

    void handleClientDataUpdate(UiUpdateData data);

    void handleServerDataUpdate(T player, BlockEntityUpdateData data);

    void fetchData(FetchData data);

    void handleServerFetchData(T player, FetchData data);

    void sendOpenContainerRequestToServer(OpenContainerRequestData request);

    void handleServerOpenContainerRequest(T player, OpenContainerRequestData request);

    void sendMakeRecipeRequestToServer(MakeRecipeRequestData data);

    void handleServerMakeRecipeRequest(T player, MakeRecipeRequestData data);

    /**
     * Client side: show the server's answer to a recipe request in the open screen (or in the chat when there is none).
     */
    void handleClientRecipeFeedback(RecipeRequestFeedback feedback);

    void sendManagementRequestToServer(RecipeManagementRequest request);

    /**
     * Run the request (only for players allowed to edit recipes) and send the resulting state back.
     */
    void handleServerManagementRequest(T player, RecipeManagementRequest request);

    /**
     * Give the state to the open screen if it is a {@link fr.en0ri4n.craftcreator.api.ui.screen.RecipeManagementListener}.
     */
    void handleClientManagementState(RecipeManagementState state);
}
