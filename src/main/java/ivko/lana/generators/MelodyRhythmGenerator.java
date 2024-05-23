package ivko.lana.generators;

import ivko.lana.musicentities.Note;
import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.PhraseType;
import ivko.lana.yaml.RhythmPattern;

import java.util.Map;

/**
 * @author Lana Ivko
 */
public class    MelodyRhythmGenerator extends RhythmGenerator
{
    private int targetTone_ = -1;
    private int previousTone_;
    private PhraseType phraseType_;
    private int correction_ = 0;

    private int channel_;

    public MelodyRhythmGenerator(Initializer initializer, PhraseType phraseType, int channel)
    {
        super(initializer, channel);
        phraseType_ = phraseType;
        channel_ = channel;
    }

    @Override
    protected RhythmPattern getRhythmPattern()
    {
        return initializer_.getMelodyRhythmPattern();
    }

    protected ISound createLastSound(int tone, int duration, int accentIndex)
    {
        ISound lastSound;
        if (targetTone_ != -1)
        {
            lastSound = createNewSound(targetTone_, duration, accents_.get(accentIndex), channel_);
        }
        else
        {
            lastSound = createNewSound(tone, duration, accents_.get(accentIndex), channel_);
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
        if (keepSameDirection())
        {
            if (phraseType_ == PhraseType.QUESTION && previousTone_ > currentTone)
            {
                correction_ = 12;
            }
            else if (phraseType_ == PhraseType.ANSWER && previousTone_ < currentTone)
            {
                correction_ = -12;
            }
        }
        previousTone_ = currentTone;
        return currentTone;
    }

    protected int getCorrection()
    {
        return correction_;
    }

    public void setTargetTone(int targetTone)
    {
        targetTone_ = targetTone;
    }

    private boolean keepSameDirection()
    {
        return Random.nextDouble() < 0.9;
    }

    public int getPreviousTone()
    {
        return previousTone_;
    }

    @Override
    protected ISound createNewSound(int tone, int duration, int accentIndex, int channel_)
    {
        Note note = new Note(tone, duration, accents_.get(accentIndex) + 10, this.channel_);
//        note.setShouldDebug(true);
        return note;
    }

    public void setPreviousTone(int previousTone)
    {
        previousTone_ = previousTone;
    }
}
