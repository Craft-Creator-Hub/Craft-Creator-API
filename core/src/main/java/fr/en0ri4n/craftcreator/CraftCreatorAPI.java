package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.api.platform.Platform;
import fr.en0ri4n.craftcreator.api.recipe.utils.RecipeInfos;
import fr.en0ri4n.craftcreator.api.recipe.serialize.RecipeInfosSerializer;
import fr.en0ri4n.craftcreator.serialize.SerializerRegistry;
import fr.en0ri4n.craftcreator.init.InitBlocks;
import fr.en0ri4n.craftcreator.init.InitItemBase;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CraftCreatorAPI {

    @Getter
    private static final CraftCreatorAPI instance = new CraftCreatorAPI();
    private static boolean initialized = false;

    @Getter
    private static final String kubeJsModId = "kubejs";

    private Platform platform;

    private CCReferences references;
    private InitBlocks initBlocks;
    private InitItemBase initItemBase;

    public void initialize(Platform platform, CCReferences references) throws CraftCreatorException {
        if (initialized) {
            throw new CraftCreatorException("CraftCreatorAPI has already been initialized !");
        }
        initialized = true;

        Objects.requireNonNull(platform, "Platform must not be null");
        this.platform = platform;

        Objects.requireNonNull(references, "References must not be null");
        this.references = references;

        // register serializers
        SerializerRegistry.register(RecipeInfos.class, new RecipeInfosSerializer());
    }
}
