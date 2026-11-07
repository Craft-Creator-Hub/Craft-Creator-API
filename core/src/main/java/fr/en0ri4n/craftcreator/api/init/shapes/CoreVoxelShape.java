package fr.en0ri4n.craftcreator.api.init.shapes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loader-agnostic representation of a voxel shape expressed as a list of AABB boxes.
 * Coordinates are in block space (0..16). Use CoreVoxelShape.box(...) or builder to create shapes.
 */
public class CoreVoxelShape {

    private final List<Box> boxes;

    private CoreVoxelShape(List<Box> boxes) {
        this.boxes = List.copyOf(boxes);
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public static CoreVoxelShape of(Box... boxes) {
        List<Box> list = new ArrayList<>();
        Collections.addAll(list, boxes);
        return new CoreVoxelShape(list);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static CoreVoxelShape single(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new CoreVoxelShape(List.of(new Box(x1, y1, z1, x2, y2, z2)));
    }

    /**
     * This shape turned 90 degrees clockwise seen from above, like a model rotated with {@code "y": 90} in a blockstate.
     */
    public CoreVoxelShape rotateY() {
        return new CoreVoxelShape(boxes.stream()
                .map(b -> new Box(16 - b.getZ2(), b.getY1(), b.getX1(), 16 - b.getZ1(), b.getY2(), b.getX2()))
                .toList());
    }

    /**
     * The shape for each horizontal facing, from the shape facing north (the default orientation of block models).
     */
    public static Map<CoreFacing, CoreVoxelShape> horizontal(CoreVoxelShape north) {
        Map<CoreFacing, CoreVoxelShape> shapes = new EnumMap<>(CoreFacing.class);
        CoreVoxelShape shape = north;
        for (CoreFacing facing : List.of(CoreFacing.NORTH, CoreFacing.EAST, CoreFacing.SOUTH, CoreFacing.WEST)) {
            shapes.put(facing, shape);
            shape = shape.rotateY();
        }
        return shapes;
    }

    public static class Builder {
        private final List<Box> boxes = new ArrayList<>();

        public Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
            boxes.add(new Box(x1, y1, z1, x2, y2, z2));
            return this;
        }

        public Builder add(Box box) {
            boxes.add(Objects.requireNonNull(box));
            return this;
        }

        public Builder addAll(CoreVoxelShape... shapes) {
            for (CoreVoxelShape shape : shapes) {
                boxes.addAll(shape.getBoxes());
            }
            return this;
        }

        public CoreVoxelShape build() {
            return new CoreVoxelShape(boxes);
        }
    }

    public static class Box {
        private final double x1, y1, z1, x2, y2, z2;

        public Box(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }

        public double getX1() { return x1; }
        public double getY1() { return y1; }
        public double getZ1() { return z1; }
        public double getX2() { return x2; }
        public double getY2() { return y2; }
        public double getZ2() { return z2; }
    }
}