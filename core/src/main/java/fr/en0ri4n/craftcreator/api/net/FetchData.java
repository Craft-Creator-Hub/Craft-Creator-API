package fr.en0ri4n.craftcreator.api.net;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.Objects;

@Getter
public class FetchData
{
    private final CoreBlockPos pos;
    private final Identifier containerId;

    public FetchData(CoreBlockPos blockPos, Identifier containerId)
    {
        this.pos = Objects.requireNonNull(blockPos, "blockPos must not be null");
        this.containerId = Objects.requireNonNull(containerId, "containerId must not be null");
    }

    @Override
    public String toString()
    {
        return "FetchData{pos=" + pos + ", containerId=" + containerId + "}";
    }
}
