package fr.en0ri4n.craftcreator;

import fr.en0ri4n.craftcreator.platform.ForgePlatform;
import fr.en0ri4n.craftcreator.utils.CraftCreatorException;
import net.minecraftforge.fml.common.Mod;

@Mod(References.MOD_ID)
public class CraftCreator
{
    private ForgePlatform platform;

    public CraftCreator() throws CraftCreatorException {
        platform = new ForgePlatform();

        CraftCreatorAPI.getInstance().initialize(platform, new References());
    }
}