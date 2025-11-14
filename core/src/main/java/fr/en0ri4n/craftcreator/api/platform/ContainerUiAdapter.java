package fr.en0ri4n.craftcreator.api.platform;

import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;

public interface ContainerUiAdapter {
    /**
     * Open a container screen for the current player using the provided model.
     * Loader implementation will:
     *  - create a Menu/Container server-side
     *  - sync to client
     *  - create and display the client Screen using model.getLayout()
     */
    void openContainer(ContainerModel model);
}