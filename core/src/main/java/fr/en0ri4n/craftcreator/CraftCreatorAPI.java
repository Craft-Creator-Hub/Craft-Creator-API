package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.init.InitBlocks;
import fr.en0ri4n.craftcreator.init.InitItemBase;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CraftCreatorAPI
{
    @Getter
    private static final Logger logger = Logger.getLogger(CraftCreatorAPI.class);

    @Getter
    private static final CraftCreatorAPI instance = new CraftCreatorAPI();
    private static boolean initialized = false;

    @Getter
    private static final String kubeJsModId = "kubejs";

    private ReferenceBase references;
    private SupportedModsBase supportedModsBase;
    private InitBlocks initBlocks;
    private InitItemBase initItemBase;

    private final List<Object> required = Arrays.asList(supportedModsBase);

    public void initialize() throws CraftCreatorException
    {
        if(initialized)
            throw new CraftCreatorException("CraftCreatorAPI has already been initialized !");
        initialized = true;


    }
}
