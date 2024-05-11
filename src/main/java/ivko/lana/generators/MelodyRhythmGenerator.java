package ivko.lana.generators;

import ivko.lana.musicentities.Note;
import ivko.lana.musicentities.ISound;

/**
 * @author Lana Ivko
 */
public class MelodyRhythmGenerator extends RhythmGenerator
{
    public MelodyRhythmGenerator(Initializer initializer)
    {
        super(initializer);
    }

    @Override
    protected ISound createNewSound(int tone, int duration, Integer accent)
    {
        return new Note(tone, duration, accent);
    }
}
