package fr.en0ri4n.craftcreator.api.init.shapes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Loader-agnostic representation of a voxel shape expressed as a list of AABB boxes.
 * Coordinates are in block space (0..16). Use CoreVoxelShape.box(...) or builder to create shapes.
 */
public final class CoreVoxelShape {

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

    public static final class Builder {
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

    public static final class Box {
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