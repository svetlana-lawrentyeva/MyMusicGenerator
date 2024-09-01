package ivko.lana.musicentities;

import ivko.lana.generators.Initializer;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class MusicFactory
{
    public static IMusic getMusic(List<Channel> channels, Initializer initializer)
    {
        if (initializer.isTibetan())
        {
            return new MusicTibetan(channels);
        }
        else
        {
            return new Music(channels, initializer);
        }
    }
}
