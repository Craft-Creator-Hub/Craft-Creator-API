package fr.en0ri4n.craftcreator.api.platform;

public interface LoggerFacade {
    void info(String msg);
    void warn(String msg);
    void error(String msg);
    void error(String msg, Throwable t);
}