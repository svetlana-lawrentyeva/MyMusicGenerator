package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.MusicType;
import ivko.lana.musicentities.Part;
import ivko.lana.musicentities.Phrase;
import ivko.lana.util.MusicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class PartGenerator implements IMusicGenerator<Part>
{
    private List<PhraseGenerator> generators_ = new ArrayList<>();
    private MusicType musicType_;

    public PartGenerator(Initializer initializer, MusicType musicType)
    {
        musicType_ = musicType;
        for (int i = 0; i < initializer.getPartsCount() + 2; ++i)
        {
            generators_.add(new PhraseGenerator(initializer, musicType));
        }
    }

    @Override
    public Part generate() throws InterruptedException
    {
        List<Phrase> phrases = new ArrayList<>();
        for (PhraseGenerator generator : generators_)
        {
            phrases.add(generator.generate());
        }
        return new Part(phrases);
    }
}
