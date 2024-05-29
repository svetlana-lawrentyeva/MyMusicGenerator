package ivko.lana.generators;

import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.Pause;
import ivko.lana.musicentities.Rhythm;
import ivko.lana.yaml.DurationProbability;
import ivko.lana.yaml.RhythmDetails;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * @author Lana Ivko
 */
public abstract class RhythmGenerator implements IMusicGenerator<Rhythm>
{
    protected static final Random Random = new Random();

    protected Initializer initializer_;
    protected RhythmDetails rhythmDetails_;
    private int measureLength_;
    protected int[] scales_;
    protected List<Integer> accents_;
    private int channel_;
    private boolean isLast_ = false;

    public RhythmGenerator(Initializer initializer, int channel)
    {
        initializer_ = initializer;
        rhythmDetails_ = getRhythmDetails();
        accents_ = rhythmDetails_.getAccents();
        measureLength_ = rhythmDetails_.getBaseDuration() * accents_.size(); // Length of one measure
        scales_ = initializer_.getScale().getScale();
        channel_ = channel;
    }

    protected int getChannel()
    {
        return channel_;
    }

    protected abstract RhythmDetails getRhythmDetails();

    public Rhythm generate() throws InterruptedException
    {
        List<ISound> sounds = new ArrayList<>();
        int tone;
        int accentIndex = 0;
        int availableMeasure = measureLength_;

        while (availableMeasure != 0)
        {
            int duration = isLast_
                    ? generateLastDuration(availableMeasure)
                    : generateSimpleDuration();
            boolean needPause = duration < 0 || needPause(availableMeasure);

            if (!needPause)
            {
                tone = getNextTone(accentIndex);
                duration = Math.min(duration, availableMeasure);
                availableMeasure -= duration;
                ISound newSound;
                if (availableMeasure == 0)
                {
                    newSound = createLastSound(tone, duration, accents_.size() - 1);
                }
                else
                {
                    newSound = createNewSound(tone, duration, accentIndex, channel_);
                }
                addNewSound(sounds, newSound);
            }
            else
            {
                duration = duration < 0 ? -duration : generateSimpleDuration();
                duration = Math.min(duration, availableMeasure);
                if (duration > 0)
                {
                    availableMeasure -= duration;
                    addNewSound(sounds, new Pause(duration, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channel_));
                }
            }
            int calculateNextAccentIndex = Math.min(accents_.size() - availableMeasure / rhythmDetails_.getBaseDuration(), accents_.size() - 1);
            accentIndex = calculateNextAccentIndex;
        }
        Rhythm rhythm = new Rhythm(sounds, channel_);
        if (rhythm.getDuration() != measureLength_)
        {
            throw new IllegalStateException(String.format("Current rhythm duration has wrong value '%s'. Rhythm duration should be '%s'", rhythm.getDuration(), measureLength_));
        }
        return rhythm;
    }

    protected void addNewSound(List<ISound> sounds, ISound newSound)
    {
        sounds.add(newSound);
    }

    protected boolean needPause(int availableMeasure)
    {
        return Random.nextInt(5) == 2;
    }

    protected int getNextTone(int accentIndex)
    {
//        int nextToneIndex = getNextTone();
//        return scales_[nextToneIndex] + getCorrection();
        return getNextTone();
    }

    protected ISound createLastSound(int tone, int duration, Integer accent)
    {
        return createNewSound(tone, duration, accent, channel_);
    }

    protected int getCorrection()
    {
        return 0;
    }

    protected int getNextTone()
    {
        return Random.nextInt(scales_.length);
    }

    protected abstract ISound createNewSound(int tone, int duration, int accentIndex, int channel_);

    protected int generateLastDuration(int availableMeasure)
    {
        return measureLength_ / 2 >= availableMeasure
                ? availableMeasure
                : generateSimpleDuration();

    }

    protected int generateSimpleDuration()
    {
        double randomValue = Random.nextDouble(); // Случайное число от 0.0 до 1.0
        double cumulativeProbability = 0.0;

        List<DurationProbability> durations = rhythmDetails_.getDurations();
        durations.sort(Comparator.comparingDouble(DurationProbability::getProbability));
        int durationResult = 0;
        for (int i = 0; i < durations.size(); i++)
        {
            DurationProbability durationProbability = durations.get(i);
            int duration = durationProbability.getDuration();
            cumulativeProbability += durationProbability.getProbability();
            if (randomValue < cumulativeProbability)
            {
                durationResult = duration;
                break;
            }
        }
        if (durationResult == 0)
        {
            durationResult = durations.get(durations.size() - 1).getDuration(); // На случай числовых ошибок, возвращаем последний элемент
        }
        return durationResult;
    }

    public void setLast()
    {
        isLast_ = true;
    }
}
