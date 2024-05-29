package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.*;
import ivko.lana.yaml.RhythmDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class PhraseGenerator implements IMusicGenerator<Phrase>
{
    private List<RhythmGenerator> generators_ = new ArrayList<>();
    private ChannelType channelType_;
    private int channel_;
    private IScale scale_;

    public PhraseGenerator(Initializer initializer, ChannelType channelType, PhraseType phraseType, int channel)
    {
        channelType_ = channelType;
        scale_ = initializer.getScale();
        channel_ = channel;
        if (channelType == ChannelType.MELODY)
        {
            createMelodyGenerators(initializer, phraseType, channel);
        }
        else
        {
            createNonMelodyGenerators(initializer, channelType, channel);
        }
    }

    private void createMelodyGenerators(Initializer initializer, PhraseType phraseType, int channel)
    {
        generators_.add(createMelodyRhythmGenerator(initializer, phraseType, RhythmType.START, channel));
        generators_.add(createMelodyRhythmGenerator(initializer, phraseType, RhythmType.BUILD, channel));
        generators_.add(createMelodyRhythmGenerator(initializer, phraseType, RhythmType.PEAK, channel));
        generators_.add(createMelodyRhythmGenerator(initializer, phraseType, RhythmType.END, channel));
    }

    private static MelodyRhythmGenerator createMelodyRhythmGenerator(Initializer initializer, PhraseType phraseType, RhythmType rhythmType, int channel)
    {
        RhythmDetails rhythmDetails = initializer.getMelodyPrimaryRhythmDetails();
        return new MelodyRhythmGenerator(initializer, phraseType, new RhythmPattern(rhythmDetails.getDurations(), rhythmDetails.getBaseDuration(),
                rhythmDetails.getAccents().size(), rhythmType), channel);
    }

    private void createNonMelodyGenerators(Initializer initializer, ChannelType channelType, int channel)
    {

        for (int i = 0; i < RhythmType.values().length; ++i)
        {
            generators_.add(
                    channelType == ChannelType.DRUM
                            ? new DrumRhythmGenerator(initializer, channel)
                            : new ChordRhythmGenerator(initializer, channel));
        }
    }

    @Override
    public Phrase generate() throws InterruptedException
    {
        List<Rhythm> rhythms = new ArrayList<>();
        int previousTone = -1;
        for (int i = 0; i < generators_.size() - 1; ++i)
        {
            RhythmGenerator generator = generators_.get(i);
            if (channelType_ == ChannelType.MELODY)
            {
                ((MelodyRhythmGenerator) generator).setPreviousTone(previousTone);
                if (i == generators_.size() - 1)
                {
                    ((MelodyRhythmGenerator) generator).setTargetTone(scale_.getBaseNote());
                }
            }
            rhythms.add(generator.generate());
            rhythms.add(RhythmSeparator.SEPARATOR);
            if (channelType_ == ChannelType.MELODY)
            {
                previousTone = ((MelodyRhythmGenerator) generator).getPreviousTone();
            }
        }
        RhythmGenerator lastGenerator = generators_.get(generators_.size() - 1);
        lastGenerator.setLast();
        rhythms.add(lastGenerator.generate());
        rhythms.add(RhythmSeparator.SEPARATOR);
        return new Phrase(rhythms, channel_);
    }
}
