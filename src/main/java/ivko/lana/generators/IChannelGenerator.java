package ivko.lana.generators;

import ivko.lana.musicentities.Channel;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public interface IChannelGenerator<T extends Channel> extends IMusicGenerator<T>
{
    Logger LOGGER = Logger.getLogger(IChannelGenerator.class.getName());

    @Override
    T generate() throws InterruptedException;
}
