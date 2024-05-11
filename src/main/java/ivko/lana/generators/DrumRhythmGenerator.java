package ivko.lana.generators;

import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.Note;
import ivko.lana.musicentities.PhraseType;
import ivko.lana.yaml.RhythmPattern;

import java.util.Map;

/**
 * @author Lana Ivko
 */
public class DrumRhythmGenerator extends RhythmGenerator
{
    private int targetTone_ = -1;
    private int previousTone_;
    private int correction_ = 0;


    public DrumRhythmGenerator(Initializer initializer)
    {
        super(initializer);
    }

    @Override
    protected RhythmPattern getRhythmPattern()
    {
        return initializer_.getMelodyRhythmPattern();
    }

    protected ISound createLastSound(int tone, int duration, Integer accent)
    {
        ISound lastSound;
        if (targetTone_ != -1)
        {
            lastSound = createNewSound(targetTone_, duration, accent);
        }
        else
        {
            lastSound = createNewSound(tone, duration, accent);
        }
        return lastSound;
    }

    @Override
    protected int getNextToneIndex()
    {
        correction_ = 0;
        if (previousTone_ == -1)
        {
            return super.getNextToneIndex();
        }
        Map<Integer, Double> nextNoteProbabilities = initializer_.getNextNoteProbabilities().getProbabilitiesByPreviousNote(previousTone_);
        double p = Random.nextDouble();
        double cumulativeProbability = 0.0;
        for (int note : scales_) {
            cumulativeProbability += nextNoteProbabilities.getOrDefault(note, 0.0);
            if (p <= cumulativeProbability) {
                return note;
            }
        }
        int currentTone = scales_[scales_.length - 1];
        previousTone_ = currentTone;
        return currentTone;
    }

    @Override
    protected ISound createNewSound(int tone, int duration, int accentIndex)
    {
        Note note = new Note(tone, duration, accents_.get(accentIndex));
//        note.setShouldDebug(true);
        return note;
    }
}
