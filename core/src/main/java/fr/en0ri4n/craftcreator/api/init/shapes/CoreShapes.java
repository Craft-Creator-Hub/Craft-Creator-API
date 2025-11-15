package fr.en0ri4n.craftcreator.api.init.shapes;

public class CoreShapes
{
    public static final CoreVoxelShape FULL = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public static class MinecraftRecipeCreatorShapes {
        public static final CoreVoxelShape SHAPE_BASE = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
        public static final CoreVoxelShape SHAPE_POST = CoreVoxelShape.single(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
        public static final CoreVoxelShape SHAPE_COMMON = CoreVoxelShape.builder().addAll(SHAPE_BASE, SHAPE_POST).build();
        public static final CoreVoxelShape SHAPE_TOP_PLATE = CoreVoxelShape.single(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
        public static final CoreVoxelShape SHAPE_COLLISION = CoreVoxelShape.builder().addAll(SHAPE_COMMON, SHAPE_TOP_PLATE).build();
        public static final CoreVoxelShape SHAPE_WEST = CoreVoxelShape.builder().addAll(CoreVoxelShape.single(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), CoreVoxelShape.single(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), CoreVoxelShape.single(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), SHAPE_COMMON).build();
        public static final CoreVoxelShape SHAPE_NORTH = CoreVoxelShape.builder().addAll(CoreVoxelShape.single(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), CoreVoxelShape.single(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), CoreVoxelShape.single(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_COMMON).build();
        public static final CoreVoxelShape SHAPE_EAST = CoreVoxelShape.builder().addAll(CoreVoxelShape.single(10.666667D, 10.0D, 0.0D, 15.0D, 14.0D, 16.0D), CoreVoxelShape.single(6.333333D, 12.0D, 0.0D, 10.666667D, 16.0D, 16.0D), CoreVoxelShape.single(2.0D, 14.0D, 0.0D, 6.333333D, 18.0D, 16.0D), SHAPE_COMMON).build();
        public static final CoreVoxelShape SHAPE_SOUTH = CoreVoxelShape.builder().addAll(CoreVoxelShape.single(0.0D, 10.0D, 10.666667D, 16.0D, 14.0D, 15.0D), CoreVoxelShape.single(0.0D, 12.0D, 6.333333D, 16.0D, 16.0D, 10.666667D), CoreVoxelShape.single(0.0D, 14.0D, 2.0D, 16.0D, 18.0D, 6.333333D), SHAPE_COMMON).build();
    }
}
