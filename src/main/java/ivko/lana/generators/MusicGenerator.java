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
    private List<IChannelGenerator> generators_ = new ArrayList<>();

    public MusicGenerator(Initializer initializer)
    {
        for (int i = 0; i < initializer.getChannelCount(); ++i)
        {
            ChannelType channelType = defineChannelType(i, initializer);
            int channelNumber = channelType == ChannelType.DRUM ? MusicUtil.DRUMS_CHANNEL_NUMBER : findChannelNumber();
            ChannelGenerator channelGenerator = new ChannelGenerator(initializer, channelType, channelNumber);
            generators_.add(channelGenerator);
        }
        if (initializer.getHertz() > 0)
        {
            generators_.add(new HertzChannelGenerator(initializer));
        }
    }

    private static ChannelType defineChannelType(int iteration, Initializer initializer)
    {
        switch (iteration)
        {
            case 0:
                return ChannelType.MELODY;
            case 1:
                if (initializer.useDrums())
                {
                    return ChannelType.DRUM;
                }
            case 2:
                if (initializer.getHertz() > 0)
                {
                    return ChannelType.HERTZ;
                }
            default:
                return ChannelType.CHORD;
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
        for (IChannelGenerator generator : generators_)
        {
            Channel channel = generator.generate();
            channels.add(channel);
        }
        return new Music(channels);
    }
}
