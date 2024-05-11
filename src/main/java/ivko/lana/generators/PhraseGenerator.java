package ivko.lana.generators;

import ivko.lana.musicentities.MusicType;
import ivko.lana.musicentities.Part;
import ivko.lana.musicentities.Phrase;
import ivko.lana.musicentities.Rhythm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class PhraseGenerator implements IMusicGenerator<Phrase>
{
    private List<RhythmGenerator> generators_ = new ArrayList<>();
    private MusicType musicType_;

    public PhraseGenerator(Initializer initializer, MusicType musicType)
    {
        musicType_ = musicType;
        for (int i = 0; i < initializer.getRhythmsCount() + 2; ++i)
        {
            generators_.add(
                    musicType == MusicType.MELODY
                            ? new MelodyRhythmGenerator(initializer)
                            : new ChordRhythmGenerator(initializer));
        }
    }

    @Override
    public Phrase generate() throws InterruptedException
    {
        List<Rhythm> rhythms = new ArrayList<>();
        for (RhythmGenerator generator : generators_)
        {
            rhythms.add(generator.generate());
        }
        return new Phrase(rhythms);
    }
}
