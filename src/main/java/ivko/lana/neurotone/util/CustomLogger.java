package ivko.lana.neurotone.util;

import ivko.lana.neurotone.StereoPlayer;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class CustomLogger
{
    public static Logger getLogger(String name)
    {
        Logger logger = Logger.getLogger(name);

        try
        {
            logger.setUseParentHandlers(false);

            for (var handler : logger.getHandlers())
            {
                logger.removeHandler(handler);
            }

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SingleLineFormatter());

            FileHandler fileHandler = new FileHandler("logs/VideoGenerator.log", true);
            fileHandler.setFormatter(new SingleLineFormatter());

            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Ошибка при создании FileHandler для логгера", e);
        }

        return logger;
    }
}
