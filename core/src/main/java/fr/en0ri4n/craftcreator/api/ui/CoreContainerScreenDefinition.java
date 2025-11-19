package fr.en0ri4n.craftcreator.api.ui;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityBehavior;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

/**
 * A logical screen made of core UI elements.
 */
@Getter
public abstract class CoreContainerScreenDefinition<T extends BlockEntityBehavior> extends CoreScreenDefinition
{
    private final ContainerModel<T> parent;
    private final T behavior;

    public CoreContainerScreenDefinition(ContainerModel<T> parent, T behavior, Identifier id, String title) {
        super(id, title);
        this.parent = parent;
        this.behavior = behavior;
    }

    public void fetchData() {
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().fetchData(new FetchData(getParent().getBlockEntityPos(), getId()));
    }

    public abstract void updateScreen(UiUpdateData data);

    @Override
    public void onClose()
    {
        super.onClose();
        JsonObject payload = new JsonObject();
        getBehavior().save(null, payload);
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendDataUpdateToServer(new BlockEntityUpdateData(parent.getBlockEntityPos(), getId(), payload));
    }
}