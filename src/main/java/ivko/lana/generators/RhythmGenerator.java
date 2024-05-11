package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.*;
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
    private static final Random random = new Random();

    protected Initializer initializer_;
    private RhythmPattern rhythmPattern_;
    private int measureLength_;
    private int[] scales_;
    private List<Integer> accents_;

    public RhythmGenerator(Initializer initializer)
    {
        initializer_ = initializer;
        rhythmPattern_ = initializer_.getRhythmPattern();
        accents_ = rhythmPattern_.getAccents();
        measureLength_ = rhythmPattern_.getBaseDuration() * accents_.size(); // Length of one measure
        scales_ = initializer_.getScale().getScales();
    }

    public Rhythm generate() throws InterruptedException
    {
        List<ISound> sounds = new ArrayList<>();
        int tone;
        int accentIndex = 0;
        int availableMeasure = measureLength_;

        while (availableMeasure != 0)
        {
            tone = IScale.BASE_NOTE + scales_[random.nextInt(scales_.length)];
            int duration = generateDuration();
            duration = Math.min(duration, availableMeasure);
            availableMeasure -= duration;
            Integer accent = accents_.get(accentIndex);
            sounds.add(createNewSound(tone, duration, accent));

            boolean needPause = random.nextInt(5) == 2;
            if (needPause && availableMeasure > 0)
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

    protected abstract ISound createNewSound(int tone, int duration, Integer accent);

    private int generateDuration()
    {
        double randomValue = random.nextDouble(); // Случайное число от 0.0 до 1.0
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
