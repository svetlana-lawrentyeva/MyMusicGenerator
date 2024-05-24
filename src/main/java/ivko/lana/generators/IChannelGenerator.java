package ivko.lana.generators;

import ivko.lana.musicentities.Channel;

/**
 * @author Lana Ivko
 */
public interface IChannelGenerator<T extends Channel> extends IMusicGenerator<T>
{
    @Override
    T generate() throws InterruptedException;
}
