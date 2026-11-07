package fr.en0ri4n.craftcreator.api.init.shapes;

/**
 * Block shapes, by mod. Shapes used with {@link CoreVoxelShape#horizontal(CoreVoxelShape)} face north.
 */
public class CoreShapes
{
    public static final CoreVoxelShape FULL = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public static class MinecraftRecipeCreatorShapes {
        public static final CoreVoxelShape SHAPE_BASE = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
        public static final CoreVoxelShape SHAPE_POST = CoreVoxelShape.single(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
        public static final CoreVoxelShape SHAPE_COMMON = CoreVoxelShape.builder().addAll(SHAPE_BASE, SHAPE_POST).build();
        public static final CoreVoxelShape SHAPE_NORTH = CoreVoxelShape.builder().addAll(CoreVoxelShape.single(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), CoreVoxelShape.single(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), CoreVoxelShape.single(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_COMMON).build();

        public static final CoreVoxelShape SHAPE_STONECUTTER = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    }

    public static class BotaniaRecipeCreatorShapes {
        public static final CoreVoxelShape SHAPE_MANA_POOL = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
        public static final CoreVoxelShape SHAPE_PURE_DAISY = CoreVoxelShape.builder()
                .add(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D)
                .add(4.0D, 3.0D, 4.0D, 12.0D, 13.0D, 12.0D)
                .build();
        public static final CoreVoxelShape SHAPE_BREWERY = CoreVoxelShape.builder()
                .add(1.0D, 0.0D, 1.0D, 15.0D, 3.0D, 15.0D)
                .add(7.0D, 3.0D, 7.0D, 9.0D, 13.0D, 9.0D)
                .add(4.0D, 13.0D, 4.0D, 12.0D, 14.0D, 12.0D)
                .build();
        public static final CoreVoxelShape SHAPE_PETAL_APOTHECARY = CoreVoxelShape.builder()
                .add(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D)
                .add(4.0D, 2.0D, 4.0D, 12.0D, 11.0D, 12.0D)
                .add(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D)
                .build();
        public static final CoreVoxelShape SHAPE_RUNIC_ALTAR = CoreVoxelShape.builder()
                .add(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D)
                .add(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D)
                .add(0.0D, 6.0D, 0.0D, 16.0D, 12.0D, 16.0D)
                .build();
        public static final CoreVoxelShape SHAPE_TERRA_PLATE = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    }

    public static class ThermalRecipeCreatorShapes {
        /** Dynamos: the base, then the coil on top */
        public static final CoreVoxelShape SHAPE_DYNAMO = CoreVoxelShape.builder()
                .add(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D)
                .add(4.0D, 10.0D, 4.0D, 12.0D, 16.0D, 12.0D)
                .build();
    }

    public static class CreateRecipeCreatorShapes {
        /** The casing only: the blade sticking out at the back has no collision, like Create's saw */
        public static final CoreVoxelShape SHAPE_MECHANICAL_SAW = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
        /** Millstone: the wide base, then the gear case and the stone */
        public static final CoreVoxelShape SHAPE_MILLSTONE = CoreVoxelShape.builder()
                .add(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D)
                .add(2.0D, 6.0D, 2.0D, 14.0D, 16.0D, 14.0D)
                .build();
        /** Depot and item drain: 13 pixels high */
        public static final CoreVoxelShape SHAPE_DEPOT = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
        public static final CoreVoxelShape SHAPE_SPOUT = CoreVoxelShape.single(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
        /** The deployer's body, without the hand */
        public static final CoreVoxelShape SHAPE_DEPLOYER = CoreVoxelShape.single(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 14.0D);
    }
}
