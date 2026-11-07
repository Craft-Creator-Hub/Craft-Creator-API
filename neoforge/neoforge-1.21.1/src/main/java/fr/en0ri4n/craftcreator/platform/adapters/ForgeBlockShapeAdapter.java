package fr.en0ri4n.craftcreator.platform.adapters;

import fr.en0ri4n.craftcreator.api.init.shapes.CoreVoxelShape;
import fr.en0ri4n.craftcreator.api.platform.BlockShapeAdapter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ForgeBlockShapeAdapter implements BlockShapeAdapter<VoxelShape>
{
    @Override
    public VoxelShape toPlatformShape(CoreVoxelShape shape) {
        List<CoreVoxelShape.Box> boxes = shape.getBoxes();

        // start empty and union all boxes
        VoxelShape result = Shapes.empty();
        for (CoreVoxelShape.Box b : boxes) {
            // Block.box expects coordinates in block-relative units (0..16)
            VoxelShape box = Block.box(b.getX1(), b.getY1(), b.getZ1(), b.getX2(), b.getY2(), b.getZ2());
            result = Shapes.or(result, box);
        }
        return result;
    }
}