package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.DrumChannel;
import ivko.lana.musicentities.Part;
import ivko.lana.util.MusicUtil;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class DrumsChannelGenerator extends AccompanimentChannelGenerator
{

    public DrumsChannelGenerator(Initializer initializer)
    {
        super(initializer);
    }

    @Override
    protected AccompanimentRhythmGenerator createRhythmGenerator(Initializer initializer)
    {
        return new DrumRhythmGenerator(initializer, MusicUtil.DRUMS_CHANNEL_NUMBER);
    }

    @Override
    protected int getChannelNumber()
    {
        return MusicUtil.DRUMS_CHANNEL_NUMBER;
    }

    protected Channel generateImpl(List<Part> parts)
    {
        return new DrumChannel(parts);
    }
}
