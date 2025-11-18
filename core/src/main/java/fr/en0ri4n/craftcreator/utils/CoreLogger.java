package fr.en0ri4n.craftcreator.utils;

import fr.en0ri4n.craftcreator.ApiReferences;
import fr.en0ri4n.craftcreator.CraftCreatorAPI;
import fr.en0ri4n.craftcreator.api.platform.LoggerFacade;

public class CoreLogger
{
    public void info(String message) {
        getLogger().info("[%s] %s".formatted(ApiReferences.MOD_NAME, message));
    }

    public void warn(String message) {
        getLogger().warn("[%s] %s".formatted(ApiReferences.MOD_NAME, message));
    }

    public void error(String message) {
        getLogger().error("[%s] %s".formatted(ApiReferences.MOD_NAME, message));
    }

    public void error(String message, Throwable throwable)
    {
        getLogger().error("[%s] %s".formatted(ApiReferences.MOD_NAME, message), throwable);
    }

    private LoggerFacade getLogger() {
        return CraftCreatorAPI.get().getPlatform().getLogger();
    }
}
