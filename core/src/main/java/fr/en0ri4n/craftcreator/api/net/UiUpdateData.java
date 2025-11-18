package fr.en0ri4n.craftcreator.api.net;

import com.google.gson.JsonObject;
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
public class UiUpdateData
{
    private final Identifier containerId;
    private final JsonObject payload;

    public UiUpdateData(Identifier containerId, JsonObject payload) {
        this.containerId = Objects.requireNonNull(containerId, "containerId");
        this.payload = payload == null ? new JsonObject() : payload;
    }

    @Override
    public String toString() {
        return "UiPacket{container=" + containerId + ", payload=" + payload + "}";
    }
}