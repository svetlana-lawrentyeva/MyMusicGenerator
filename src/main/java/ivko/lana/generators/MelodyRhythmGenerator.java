package ivko.lana.generators;

import ivko.lana.musicentities.*;
import ivko.lana.yaml.RhythmDetails;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class MelodyRhythmGenerator extends RhythmGenerator
{
    private int targetTone_ = -1;
    private int previousTone_;
    private PhraseType phraseType_;
    private int correction_ = 0;

    private RhythmPattern rhythmPattern_;

    private int channel_;
    private Iterator<Integer> rhythmPatternIterator_;

    public MelodyRhythmGenerator(Initializer initializer, PhraseType phraseType, RhythmPattern rhythmPattern, int channel)
    {
        super(initializer, channel);
        phraseType_ = phraseType;
        rhythmPattern_ = rhythmPattern;
        channel_ = channel;
        rhythmPatternIterator_ = rhythmPattern.getPattern().iterator();
    }

    @Override
    protected int generateSimpleDuration()
    {
        if (rhythmPatternIterator_.hasNext())
        {
            return rhythmPatternIterator_.next();
        }
        return super.generateSimpleDuration();
    }

    @Override
    protected RhythmDetails getRhythmDetails()
    {
        return initializer_.getMelodyPrimaryRhythmDetails();
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
    protected int getNextTone()
    {
        if (previousTone_ == -1)
        {
            return super.getNextTone();
        }
        RhythmType rhythmType = rhythmPattern_.getRhythmType();
        int[] steps = rhythmType.getSteps(phraseType_);
        return phraseType_ == PhraseType.QUESTION
                ? getNextToneForQuestion(steps)
                : getNextToneForAnswer(steps);
    }

    public int getNextToneForQuestion(int[] steps)
    {
        return keepSameDirection()
                ? findNextToneIn(steps)
                : findPreviousToneIn(steps);
    }

    public int getNextToneForAnswer(int[] steps)
    {
        return keepSameDirection()
                ? findPreviousToneIn(steps)
                : findNextToneIn(steps);
    }

    private int findNextToneIn(int[] steps)
    {
        int correction = 0;
        int currentTone = previousTone_;
        int minTone = scales_[0];
        int maxTone = scales_[scales_.length - 1];
        while (true)
        {
            if (!(currentTone < minTone)) break;
            correction = 12;
            currentTone += 12;
        }
        while (true)
        {
            if (!(currentTone > maxTone)) break;
            correction = -12;
            currentTone -= 12;
        }
        for (int step : steps)
        {
            int suggestedTone = currentTone + step;
            int checkedTone = suggestedTone;
            while (checkedTone < minTone)
            {
                checkedTone += 12;
            }
            while (checkedTone > maxTone)
            {
                checkedTone -= 12;
            }
            if (Arrays.binarySearch(scales_, checkedTone) >= 0)
            {
                return suggestedTone + correction;
            }
        }
        return getNextToneOld();
    }

    private int findPreviousToneIn(int[] steps)
    {
        int correction = 0;
        int currentTone = previousTone_;
        int minTone = scales_[0];
        int maxTone = scales_[scales_.length - 1];
        while (true)
        {
            if (!(currentTone < minTone)) break;
            correction = 12;
            currentTone += 12;
        }
        while (true)
        {
            if (!(currentTone > maxTone)) break;
            correction = -12;
            currentTone -= 12;
        }
        for (int step : steps)
        {
            int suggestedTone = currentTone - step;
            int checkedTone = suggestedTone;
            while (checkedTone < minTone)
            {
                checkedTone += 12;
            }
            while (checkedTone > maxTone)
            {
                checkedTone -= 12;
            }
            if (Arrays.binarySearch(scales_, checkedTone) >= 0)
            {
                return suggestedTone + correction;
            }
        }
        return getNextToneOld();
    }

    protected int getNextToneOld()
    {
        correction_ = 0;
        if (previousTone_ == -1)
        {
            return super.getNextTone();
        }
        Map<Integer, Double> nextNoteProbabilities = initializer_.getNextNoteProbabilities().getProbabilitiesByPreviousNote(previousTone_);
        double p = Random.nextDouble();
        double cumulativeProbability = 0.0;
        for (int note : scales_)
        {
            cumulativeProbability += nextNoteProbabilities.getOrDefault(note, 0.0);
            if (p <= cumulativeProbability)
            {
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
        int baseDurationMultiplier = initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier();
        ISound sound = new Note(tone, duration, accents_.get(accentIndex) + 10, this.channel_, baseDurationMultiplier);

//        note.setShouldDebug(true);
        return sound;
    }

    public void setPreviousTone(int previousTone)
    {
        previousTone_ = previousTone;
    }
}
