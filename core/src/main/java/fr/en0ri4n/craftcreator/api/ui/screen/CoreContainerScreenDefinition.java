package fr.en0ri4n.craftcreator.api.ui.screen;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.blockentity.BlockEntityBehavior;
import fr.en0ri4n.craftcreator.api.net.BlockEntityUpdateData;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import fr.en0ri4n.craftcreator.api.net.UiUpdateData;
import fr.en0ri4n.craftcreator.api.render.RenderContext;
import fr.en0ri4n.craftcreator.api.ui.container.ContainerModel;
import fr.en0ri4n.craftcreator.api.ui.elements.Core2DBounds;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

/**
 * A logical screen made of core UI elements.
 */
@Getter
public abstract class CoreContainerScreenDefinition<T extends BlockEntityBehavior> extends CoreScreenDefinition<CoreContainerScreenDefinition.CoreContainerScreenData<T>>
{
    private final ContainerModel<T> parentContainerModel;

    public CoreContainerScreenDefinition(ContainerModel<T> parent, T behavior, Identifier id, String title)
    {
        super(id, title, new CoreContainerScreenData<>(behavior), Core2DBounds.ofSize(parent.getLayout().getWidth(), parent.getLayout().getHeight()));
        this.parentContainerModel = parent;
    }

    @Override
    public void fetchData()
    {
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().fetchData(new FetchData(getParentContainerModel().getBlockEntityPos(), getId()));
    }

    @Override
    public void renderBackground(RenderContext ctx)
    {
        super.renderBackground(ctx);
        Core2DBounds size = getBackgroundTextureSize();

        getCurrentRenderAdapter().drawTexture(ctx, getBackgroundTexture(),
                getGuiSize().getX(), getGuiSize().getY(), size.getWidth(), size.getHeight(),
                256, 256,
                0, 0, getGuiSize().getWidth(), getGuiSize().getHeight());
    }

    public abstract Identifier getBackgroundTexture();

    @Override
    public void sendUpdates()
    {
        JsonObject payload = new JsonObject();
        getScreenData().getBehavior().save(null, payload);
        CraftCreatorAPI.get().getPlatform().getNetworkInteractionAdapter().sendDataUpdateToServer(new BlockEntityUpdateData(parentContainerModel.getBlockEntityPos(), getId(), payload));
    }

    @Override
    public final void updateScreen(UiUpdateData data)
    {
        super.updateScreen(data);
        onDataUpdated(getScreenData().getBehavior());
    }

    protected T getBehavior()
    {
        return getScreenData().getBehavior();
    }

    protected abstract void onDataUpdated(T behavior);

    @Getter
    public static class CoreContainerScreenData<T extends BlockEntityBehavior> implements ScreenData
    {
        private final T behavior;

        public CoreContainerScreenData(T behavior)
        {
            this.behavior = behavior;
        }

        @Override
        public void load(JsonObject payload)
        {
            this.behavior.load(null, payload);
        }

        @Override
        public JsonObject save()
        {
            JsonObject payload = new JsonObject();
            this.behavior.save(null, payload);
            return payload;
        }
    }
}