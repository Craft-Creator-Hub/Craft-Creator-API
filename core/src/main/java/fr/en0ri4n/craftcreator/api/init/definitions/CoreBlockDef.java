package fr.en0ri4n.craftcreator.api.init.definitions;

import fr.en0ri4n.craftcreator.api.init.shapes.CoreFacing;
import fr.en0ri4n.craftcreator.api.init.shapes.CoreVoxelShape;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;

import java.util.*;

/**
 * Loader-agnostic description of a block to register.
 * Includes an optional facing/orientation hint used by adapters.
 */
public class CoreBlockDef {

    @Getter
    private final Identifier id;
    @Getter
    private final Map<String, String> properties;
    @Getter
    private final FacingType facingType;
    private final String defaultFacing; // optional, e.g. "north" or "y"
    @Getter
    private final boolean rotateModel;  // whether to ask model to rotate with blockstate
    @Getter
    private final Map<CoreFacing, CoreVoxelShape> facingShapes;
    @Getter
    private final CoreVoxelShape blockShape; // shape for blocks without facing, can be null if using facingShapes

    private CoreBlockDef(Identifier id,
                         Map<String, String> properties,
                         FacingType facingType,
                         String defaultFacing,
                         boolean rotateModel,
                         Map<CoreFacing, CoreVoxelShape> facingShape,
                         CoreVoxelShape blockShape) {
        this.id = Objects.requireNonNull(id, "id");
        this.properties = properties == null ? Collections.emptyMap() : Map.copyOf(properties);
        this.facingType = facingType == null ? FacingType.NONE : facingType;
        this.defaultFacing = defaultFacing;
        this.rotateModel = rotateModel;
        this.facingShapes = facingShape == null ? Collections.emptyMap() : Map.copyOf(facingShape);
        this.blockShape = blockShape;
    }

    public Optional<String> getDefaultFacing() { return Optional.ofNullable(defaultFacing); }

    public static Builder builder(Identifier id) { return new Builder(id); }

    public static class Builder {
        private final Identifier id;
        private final Map<String, String> props = new HashMap<>();
        private FacingType facingType = FacingType.NONE;
        private String defaultFacing = null;
        private Map<CoreFacing, CoreVoxelShape> facingShape = null;
        private CoreVoxelShape blockShape = null;

        public Builder(Identifier id) { this.id = id; }

        public Builder property(String key, String value) { props.put(key, value); return this; }

        public Builder facing(FacingType facingType) { this.facingType = facingType; return this; }

        /** defaultFacing examples: "north", "south", "up", "down", "x", "y", "z" depending on facingType */
        public Builder defaultFacing(String defaultFacing) { this.defaultFacing = defaultFacing; return this; }

        public Builder facingShapes(Map<CoreFacing, CoreVoxelShape> facingShape) { this.facingShape = facingShape; return this; }

        public Builder blockShape(CoreVoxelShape blockShape) { this.blockShape = blockShape; return this; }

        public CoreBlockDef build() {
            return new CoreBlockDef(id, props, facingType, defaultFacing, facingType != FacingType.NONE, facingShape, blockShape);
        }
    }
}