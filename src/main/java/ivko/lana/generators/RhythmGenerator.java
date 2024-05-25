package ivko.lana.generators;

import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.Pause;
import ivko.lana.musicentities.Rhythm;
import ivko.lana.yaml.DurationProbability;
import ivko.lana.yaml.RhythmPattern;

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
    protected RhythmPattern rhythmPattern_;
    private int measureLength_;
    protected int[] scales_;
    protected List<Integer> accents_;
    private int channel_;

    public RhythmGenerator(Initializer initializer, int channel)
    {
        initializer_ = initializer;
        rhythmPattern_ = getRhythmPattern();
        accents_ = rhythmPattern_.getAccents();
        measureLength_ = rhythmPattern_.getBaseDuration() * accents_.size(); // Length of one measure
        scales_ = initializer_.getScale().getScale();
        channel_ = channel;
    }

    protected int getChannel()
    {
        return channel_;
    }

    protected abstract RhythmPattern getRhythmPattern();

    public Rhythm generate() throws InterruptedException
    {
        List<ISound> sounds = new ArrayList<>();
        int tone;
        int accentIndex = 0;
        int availableMeasure = measureLength_;

        while (availableMeasure != 0)
        {
            tone = getNextTone(accentIndex);
            int duration = generateDuration();
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
            sounds.add(newSound);

            boolean needPause = Random.nextInt(5) == 2 || availableMeasure != 0;
            if (needPause)
            {
                duration = generateDuration();
                duration = Math.min(duration, availableMeasure);
                availableMeasure -= duration;
                sounds.add(new Pause(duration, initializer_.getMelodyRhythmPattern().getBaseDurationMultiplier(), channel_));
            }
            accentIndex = Math.min(accents_.size() - availableMeasure / rhythmPattern_.getBaseDuration(), accents_.size() - 1);
        }
        Rhythm rhythm = new Rhythm(sounds, channel_);
        if (rhythm.getDuration() != measureLength_)
        {
            throw new IllegalStateException(String.format("Current rhythm duration has wrong value '%s'. Rhythm duration should be '%s'", rhythm.getDuration(), measureLength_));
        }
        return rhythm;
    }

    protected int getNextTone(int accentIndex)
    {
        return scales_[getNextToneIndex()] + getCorrection();
    }

    protected ISound createLastSound(int tone, int duration, Integer accent)
    {
        return createNewSound(tone, duration, accent, channel_);
    }

    protected int getCorrection()
    {
        return 0;
    }

    protected int getNextToneIndex()
    {
        return Random.nextInt(scales_.length);
    }

    protected abstract ISound createNewSound(int tone, int duration, int accentIndex, int channel_);

    protected int generateDuration()
    {
        double randomValue = Random.nextDouble(); // Случайное число от 0.0 до 1.0
        double cumulativeProbability = 0.0;

        List<DurationProbability> durations = rhythmPattern_.getDurations();
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
}
