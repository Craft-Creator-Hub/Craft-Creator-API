package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.api.net.MakeRecipeRequestData;
import fr.en0ri4n.craftcreator.api.net.OpenContainerRequestData;
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
}
