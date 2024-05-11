package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.DrumChannel;
import ivko.lana.musicentities.MusicType;
import ivko.lana.musicentities.Part;
import ivko.lana.util.MusicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class ChannelGenerator implements IMusicGenerator<Channel>
{
    private List<PartGenerator> generators_ = new ArrayList<>();
    private MusicType musicType_;

    public ChannelGenerator(Initializer initializer, MusicType musicType)
    {
        musicType_ = musicType;
        generators_.add(new PartGenerator(initializer, musicType));
        for (int i = 0; i < initializer.getPartsCount() + 2; ++i)
        {
            generators_.add(new PartGenerator(initializer, musicType));
        }
        generators_.add(new PartGenerator(initializer, musicType));
    }

    @Override
    public Channel generate() throws InterruptedException
    {
        List<Part> parts = new ArrayList<>();
        for (PartGenerator generator : generators_)
        {
            parts.add(generator.generate());
        }
        return musicType_ == MusicType.DRUM
        ? new DrumChannel(parts, MusicUtil.getInstance().getNextInstrumentCode(false))
        : new Channel(parts, MusicUtil.getInstance().getNextInstrumentCode(musicType_ == MusicType.MELODY));
    }
}
