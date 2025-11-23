package fr.en0ri4n.craftcreator.utils;

import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.platform.LoggerFacade;

public class CoreLogger
{
    public void info(String message)
    {
        getLogger().info(message);
    }

    public void warn(String message)
    {
        getLogger().warn(message);
    }

    public void error(String message)
    {
        getLogger().error(message);
    }

    public void error(String message, Throwable throwable)
    {
        getLogger().error(message, throwable);
    }

    private LoggerFacade getLogger()
    {
        return CraftCreatorAPI.get().getPlatform().getLogger();
    }
}
