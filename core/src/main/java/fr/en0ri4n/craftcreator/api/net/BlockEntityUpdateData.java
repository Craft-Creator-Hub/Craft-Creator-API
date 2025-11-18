package fr.en0ri4n.craftcreator.api.net;

import com.google.gson.JsonObject;
import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.Objects;

/**
 * Generic UI packet payload (core module). Platform code will serialize/deserialize this
 * when sending over the network.
 * <p>
 * This is intentionally minimal and serializable via JSON.
 */
@Getter
public class BlockEntityUpdateData
{
    private final CoreBlockPos pos;
    private final Identifier containerId;
    private final JsonObject payload;

    public BlockEntityUpdateData(CoreBlockPos pos, Identifier containerId, JsonObject payload) {
        this.pos = Objects.requireNonNull(pos, "pos");
        this.containerId = Objects.requireNonNull(containerId, "containerId");
        this.payload = payload == null ? new JsonObject() : payload;
    }

    @Override
    public String toString() {
        return "UiPacket{pos=" + pos + ", container=" + containerId + ", payload=" + payload + "}";
    }
}