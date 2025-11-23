package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.net.*;

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
}
