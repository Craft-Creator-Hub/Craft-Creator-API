package fr.en0ri4n.craftcreator.api.net;

import fr.en0ri4n.craftcreator.api.init.definitions.CoreBlockPos;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.Objects;

@Getter
public class MakeRecipeRequestData
{
    private final CoreBlockPos pos;
    private final Identifier containerId;
    /** Only make the recipe's json and send it back, without exporting the recipe */
    private final boolean preview;

    public MakeRecipeRequestData(CoreBlockPos pos, Identifier containerId)
    {
        this(pos, containerId, false);
    }

    public MakeRecipeRequestData(CoreBlockPos pos, Identifier containerId, boolean preview)
    {
        this.pos = Objects.requireNonNull(pos, "pos cannot be null");
        this.containerId = Objects.requireNonNull(containerId, "containerId cannot be null");
        this.preview = preview;
    }

    @Override
    public String toString()
    {
        return "MakeRecipeData{pos=" + pos + ", containerId=" + containerId + ", preview=" + preview + '}';
    }
}
