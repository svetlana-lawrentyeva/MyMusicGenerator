package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.Music;
import ivko.lana.musicentities.ChannelType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class MusicGenerator implements IMusicGenerator<Music>
{
    private List<ChannelGenerator> generators_ = new ArrayList<>();

    public MusicGenerator(Initializer initializer)
    {
        for (int i = 0; i < initializer.getChannelCount(); ++i)
        {
            ChannelGenerator channelGenerator = new ChannelGenerator(
                    initializer,
                    i == 0
                        ? ChannelType.MELODY
                        : i == 1
                            ? ChannelType.CHORD
                            : ChannelType.DRUM
            );
            generators_.add(channelGenerator);
        }
    }

    @Override
    public Music generate() throws InterruptedException
    {
        List<Channel> channels = new ArrayList<>();
        for (ChannelGenerator generator : generators_)
        {
            channels.add(generator.generate());
        }
        return new Music(channels);
    }
}
