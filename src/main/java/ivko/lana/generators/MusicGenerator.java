package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.Music;
import ivko.lana.musicentities.ChannelType;
import ivko.lana.util.MusicUtil;

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
            ChannelType channelType = i == 0
                    ? ChannelType.MELODY
                    : i == 1
                    ? ChannelType.DRUM
                    : ChannelType.CHORD;
            int channelNumber = channelType == ChannelType.DRUM ? 9 : findChannelNumber();
            ChannelGenerator channelGenerator = new ChannelGenerator(initializer, channelType, channelNumber);
            generators_.add(channelGenerator);
        }
    }

    protected int findChannelNumber()
    {
        return MusicUtil.getInstance().getFreeChannelNumber();
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
