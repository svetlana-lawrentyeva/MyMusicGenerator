package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class PhraseGenerator implements IMusicGenerator<Phrase>
{
    private List<RhythmGenerator> generators_ = new ArrayList<>();
    private ChannelType channelType_;
    private PhraseType phraseType_;

    private IScale scale_;

    public PhraseGenerator(Initializer initializer, ChannelType channelType, PhraseType phraseType, int channel)
    {
        channelType_ = channelType;
        phraseType_ = phraseType;
        scale_ = initializer.getScale();
        int rhythmsCount = initializer.getRhythmsCount() + 2;
        for (int i = 0; i < rhythmsCount; ++i)
        {
            generators_.add(
                    channelType == ChannelType.MELODY
                            ? new MelodyRhythmGenerator(initializer, phraseType_, channel)
                            : channelType == ChannelType.DRUM
                            ? new DrumRhythmGenerator(initializer, channel)
                            : new ChordRhythmGenerator(initializer, channel));
        }
    }

    @Override
    public Phrase generate() throws InterruptedException
    {
        List<Rhythm> rhythms = new ArrayList<>();
        int previousTone = -1;
        for (int i = 0; i < generators_.size(); ++i)
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
        return new Phrase(rhythms);
    }
}
