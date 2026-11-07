package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.init.shapes.CoreFacing;
import fr.en0ri4n.craftcreator.api.platform.FacingAdapter;
import net.minecraft.core.Direction;

public class ForgeFacingAdapter implements FacingAdapter<Direction>
{
    @Override
    public CoreFacing toCore(Direction platformFacing)
    {
        return switch(platformFacing) {
            case DOWN -> CoreFacing.DOWN;
            case UP -> CoreFacing.UP;
            case NORTH -> CoreFacing.NORTH;
            case SOUTH -> CoreFacing.SOUTH;
            case WEST -> CoreFacing.WEST;
            case EAST -> CoreFacing.EAST;
        };
    }

    @Override
    public Direction fromCore(CoreFacing coreFacing)
    {
        return switch(coreFacing) {
            case DOWN -> Direction.DOWN;
            case UP -> Direction.UP;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case EAST -> Direction.EAST;
        };
    }
}
