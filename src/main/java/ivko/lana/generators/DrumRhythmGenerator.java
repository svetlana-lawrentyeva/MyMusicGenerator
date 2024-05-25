package ivko.lana.generators;

import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.MusicType;
import ivko.lana.musicentities.Note;
import ivko.lana.yaml.RhythmPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class DrumRhythmGenerator extends AccompanimentRhythmGenerator
{
    protected List<Integer> drums_;
    private List<Integer> drumSequence_ = new ArrayList<>();
    int currentDrumIndex = 0;

    public DrumRhythmGenerator(Initializer initializer, int channel)
    {
        super(initializer, channel);
        drums_ = initializer.getMusicType() == MusicType.EPIC
                ? rhythmPattern_.getDrums(initializer.getMusicType())
                : initializer_.getDrumCombinations();
        RhythmPattern rhythmPattern = initializer_.getChordRhythmPattern();
        int baseDuration = rhythmPattern.getBaseDuration();
        int baseCounter = rhythmPattern.getAccents().size();
        int measureLength = baseCounter * baseDuration;
        int drumTick = baseDuration / 4;
        int drumTickCounter = measureLength / drumTick;
        for (int i = 0; i < drumTickCounter; ++i)
        {
            int drumsIndex = (int) (Math.random() * drums_.size());
            drumSequence_.add(drumsIndex);
        }
    }

    @Override
    protected RhythmPattern getRhythmPattern()
    {
        return initializer_.getMelodyRhythmPattern();
    }

    @Override
    protected int generateDuration()
    {
        return initializer_.getChordRhythmPattern().getBaseDuration() / 4;
    }

    @Override
    protected ISound createNewSound(int tone, int duration, int accentIndex, int channel_)
    {
        int drumsIndex = currentDrumIndex++;
        if (drumsIndex >= drums_.size())
        {
            drumsIndex = 0;
            currentDrumIndex = 0;
        }
        Note note = new Note(drums_.get(drumsIndex), duration, accents_.get(accentIndex), getChannel(), initializer_.getMelodyRhythmPattern().getBaseDurationMultiplier());
        note.setShouldDebug(false);
        return note;
    }
}
