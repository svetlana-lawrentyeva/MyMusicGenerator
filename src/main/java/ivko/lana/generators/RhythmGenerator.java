package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.Pause;
import ivko.lana.musicentities.Rhythm;
import ivko.lana.yaml.DurationProbability;
import ivko.lana.yaml.RhythmPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Lana Ivko
 */
public abstract class RhythmGenerator implements IMusicGenerator<Rhythm>
{
    protected static final Random Random = new Random();

    protected Initializer initializer_;
    private RhythmPattern rhythmPattern_;
    private int measureLength_;
    protected int[] scales_;
    protected List<Integer> accents_;

    public RhythmGenerator(Initializer initializer)
    {
        initializer_ = initializer;
        rhythmPattern_ = getRhythmPattern();
        accents_ = rhythmPattern_.getAccents();
        measureLength_ = rhythmPattern_.getBaseDuration() * accents_.size(); // Length of one measure
        scales_ = initializer_.getScale().getScales();
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
            tone = IScale.BASE_NOTE + scales_[getNextToneIndex()] + getCorrection();
            int duration = generateDuration();
            duration = Math.min(duration, availableMeasure);
            availableMeasure -= duration;
            Integer accent = accents_.get(accentIndex);
            ISound newSound;
            if (availableMeasure == 0)
            {
                newSound = createLastSound(tone, duration, accent);
            }
            else
            {
                newSound = createNewSound(tone, duration, accent);
            }
            sounds.add(newSound);

            boolean needPause = Random.nextInt(5) == 2 || availableMeasure != 0;
            if (needPause)
            {
                duration = generateDuration();
                duration = Math.min(duration, availableMeasure);
                availableMeasure -= duration;
                sounds.add(new Pause(duration));
            }
            accentIndex = accents_.size() - availableMeasure / rhythmPattern_.getBaseDuration();
        }
        Rhythm rhythm = new Rhythm(sounds);
        if (rhythm.getDuration() != measureLength_)
        {
            throw new IllegalStateException(String.format("Current rhythm duration has wrong value '%s'. Rhythm duration should be '%s'", rhythm.getDuration(), measureLength_));
        }
        return rhythm;
    }

    protected ISound createLastSound(int tone, int duration, Integer accent)
    {
        return createNewSound(tone, duration, accent);
    }

    protected int getCorrection()
    {
        return 0;
    }

    protected int getNextToneIndex()
    {
        return Random.nextInt(scales_.length);
    }

    protected abstract ISound createNewSound(int tone, int duration, int accentIndex);

    private int generateDuration()
    {
        double randomValue = Random.nextDouble(); // Случайное число от 0.0 до 1.0
        double cumulativeProbability = 0.0;

        List<DurationProbability> durations = rhythmPattern_.getDurations();
        for (int i = 0; i < durations.size(); i++)
        {
            DurationProbability durationProbability = durations.get(i);
            int duration = durationProbability.getDuration();
            cumulativeProbability += durationProbability.getProbability();
            if (randomValue < cumulativeProbability)
            {
                return duration;
            }
        }
        return durations.get(durations.size() - 1).getDuration(); // На случай числовых ошибок, возвращаем последний элемент
    }
}
