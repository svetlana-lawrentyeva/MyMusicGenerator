package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.ChannelType;
import ivko.lana.musicentities.DrumChannel;
import ivko.lana.musicentities.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class ChannelGenerator implements IMusicGenerator<Channel>
{
    private static final Logger LOGGER = Logger.getLogger(ChannelGenerator.class.getName());
    private List<PartGenerator> generators_ = new ArrayList<>();
    private ChannelType channelType_;
    private Initializer initializer_;

    public ChannelGenerator(Initializer initializer, ChannelType channelType)
    {
        initializer_ = initializer;
        channelType_ = channelType;
        generators_.add(new PartGenerator(initializer, channelType));
        for (int i = 0; i < initializer.getPartsCount() + 2; ++i)
        {
            generators_.add(new PartGenerator(initializer, channelType));
        }
        generators_.add(new PartGenerator(initializer, channelType));
    }

    @Override
    public Channel generate() throws InterruptedException
    {
        List<Part> parts = new ArrayList<>();
        for (PartGenerator generator : generators_)
        {
            parts.add(generator.generate());
        }
        int nextInstrumentCode;
        if (channelType_ == ChannelType.MELODY)
        {
            nextInstrumentCode = initializer_.getMelodyInstrumentCode();
        }
        else
        {
            List<Integer> chordInstrumentCodes = initializer_.getChordInstrumentCodes();
            int chordInstrumentIndex = (int) (Math.random() * chordInstrumentCodes.size());
            nextInstrumentCode = chordInstrumentCodes.get(chordInstrumentIndex);
        }
        Channel channel = channelType_ == ChannelType.DRUM
                ? new DrumChannel(parts, 0)
                : new Channel(parts, nextInstrumentCode);
        if (channelType_ == ChannelType.MELODY)
        {
            channel.setIsMelody(true);
        }

        LOGGER.info(String.format("channel %s '%s' for type '%s' with InstrumentCode '%s' is created", channel.getClass().getSimpleName(), channel.hashCode(), channelType_, channel.getInstrumentCode()));
        return channel;
    }
}
