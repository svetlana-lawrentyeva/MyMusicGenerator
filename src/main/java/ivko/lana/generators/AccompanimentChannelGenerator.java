package ivko.lana.generators;

import ivko.lana.musicentities.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public abstract class AccompanimentChannelGenerator implements IChannelGenerator
{
    protected Initializer initializer_;

    private AccompanimentRhythmGenerator rhythmGenerator_;
    protected Channel melodyChannel_;

    public AccompanimentChannelGenerator(Initializer initializer)
    {
        initializer_ = initializer;
        rhythmGenerator_ = createRhythmGenerator(initializer);
    }

    protected abstract AccompanimentRhythmGenerator createRhythmGenerator(Initializer initializer);

    public void setMelodyChannel(Channel melodyChannel)
    {
        melodyChannel_ = melodyChannel;
    }

    @Override
    public Channel generate() throws InterruptedException
    {
        List<Part> parts = melodyChannel_.getParts().stream()
                .map(this::generatePart)
                .collect(Collectors.toList());
        Channel channel = generateImpl(parts);

        LOGGER.info(String.format("channel %s '%s' with InstrumentCode '%s' is created", channel.getClass().getSimpleName(), channel.hashCode(), channel.getInstrumentCode()));
        return channel;
    }

    protected abstract Channel generateImpl(List<Part> parts);

    private Part generatePart(Part part)
    {
        List<Phrase> phrases = part.getPhrases().stream()
                .map(this::generatePhrase)
                .collect(Collectors.toList());
        return new Part(phrases, getChannelNumber());
    }

    private Phrase generatePhrase(Phrase phrase)
    {
        List<Rhythm> rhythms = phrase.getRhythms().stream()
                .map(this::generateRhythm)
                .collect(Collectors.toList());
        return new Phrase(rhythms, getChannelNumber());
    }

    protected abstract int getChannelNumber();

    protected Rhythm generateRhythm(Rhythm rhythm)
    {
        if (rhythm == RhythmSeparator.SEPARATOR)
        {
            return RhythmSeparator.SEPARATOR;
        }
        Rhythm generatedRhythm = null;
        try
        {
            rhythmGenerator_.setMelodyRhythm(rhythm);
            generatedRhythm = rhythmGenerator_.generate();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return generatedRhythm;
    }

}
