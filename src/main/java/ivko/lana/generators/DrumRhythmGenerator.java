package ivko.lana.generators;

import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.MusicType;
import ivko.lana.musicentities.Note;
import ivko.lana.yaml.RhythmPattern;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class DrumRhythmGenerator extends RhythmGenerator
{
    protected List<Integer> drums_;

    public DrumRhythmGenerator(Initializer initializer, int channel)
    {
        super(initializer, channel);
        drums_ = initializer.getMusicType() == MusicType.EPIC
                ? rhythmPattern_.getDrums(initializer.getMusicType())
                : initializer_.getDrumCombinations();
    }

    @Override
    protected RhythmPattern getRhythmPattern()
    {
        return initializer_.getMelodyRhythmPattern();
    }

    @Override
    protected ISound createNewSound(int tone, int duration, int accentIndex, int channel_)
    {
        int drumsIndex = (int) (Math.random() * drums_.size());
        Note note = new Note(drums_.get(drumsIndex), duration, accents_.get(accentIndex), getChannel());
        note.setShouldDebug(false);
        return note;
    }
}
