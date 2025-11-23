package fr.en0ri4n.craftcreator.api.net;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.Objects;

@Getter
public class OpenContainerRequestData
{
    private final CoreBlockPos blockPos;
    private final Identifier containerId;

    public OpenContainerRequestData(CoreBlockPos pos, Identifier containerId)
    {
        this.blockPos = Objects.requireNonNull(pos, "blockPos cannot be null");
        this.containerId = Objects.requireNonNull(containerId, "containerId cannot be null");
    }

    @Override
    public String toString()
    {
        return "OpenContainerRequest{blockPos=" + blockPos + ", containerId=" + containerId + '}';
    }
}
