package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.ChannelType;
import ivko.lana.musicentities.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class MelodyChannelGenerator implements IChannelGenerator
{
    private List<PartGenerator> generators_ = new ArrayList<>();
    private Initializer initializer_;
    private int channelNumber_;

    public MelodyChannelGenerator(Initializer initializer, int channelNumber)
    {
        initializer_ = initializer;
        channelNumber_ = channelNumber;

        for (int i = 0; i < initializer.getPartsCount(); ++i)
        {
            generators_.add(new PartGenerator(initializer, ChannelType.MELODY, channelNumber_));
        }
    }

    @Override
    public Channel generate() throws InterruptedException
    {
        List<Part> parts = generateParts();
        int nextInstrumentCode = getInstrumentCode();
        Channel channel = createChannel(parts, nextInstrumentCode);

        LOGGER.info(String.format("channel %s '%s' for type '%s' with InstrumentCode '%s' is created", channel.getClass().getSimpleName(), channel.hashCode(), ChannelType.MELODY, channel.getInstrumentCode()));
        return channel;
    }

    private Channel createChannel(List<Part> parts, int instrumentCode)
    {
        Channel channel = new Channel(parts, instrumentCode, channelNumber_);
        channel.setIsMelody(true);
        return channel;
    }

    private List<Part> generateParts() throws InterruptedException
    {
        List<Part> parts = new ArrayList<>();
        for (PartGenerator generator : generators_)
        {
            parts.add(generator.generate());
        }
        return parts;
    }

    private int getInstrumentCode()
    {
        return initializer_.getMelodyInstrumentCode();
    }
}
