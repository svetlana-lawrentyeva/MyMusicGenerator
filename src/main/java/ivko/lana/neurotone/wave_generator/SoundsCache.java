package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;
import ivko.lana.neurotone.wave_generator.sounds.Sound;
import ivko.lana.neurotone.wave_generator.sounds.SoundType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class SoundsCache
{
    private final Sound[] cache_;
    private final Map<Integer, SoundDetails> soundDetailsMap_;
    private SoundType soundType_;

    public SoundsCache(SoundType soundType, double frequency, int durationMs, boolean isLeft)
    {
        soundType_ = soundType;
        IOvertoneHelper overtoneHelper = soundType_.getOvertoneHelper();
        double[] multipliers = durationMs == 0 ? overtoneHelper.getHitMultipliers() : overtoneHelper.getMultipliers();
        double[] amplitudes = durationMs == 0 ? overtoneHelper.getHitAmplitudes() : overtoneHelper.getAmplitudes();
        cache_ = new Sound[durationMs == 0 ? 1 : multipliers.length];
        soundDetailsMap_ = new HashMap<>();
        for (int i = 0; i < multipliers.length; ++i)
        {
            double multiplier = multipliers[i];
            double amplitude = amplitudes[i];
            double overtoneFrequency = frequency * multiplier;

            soundDetailsMap_.put(i, new SoundDetails(overtoneFrequency, amplitude, durationMs, isLeft));
        }
    }

    public int getSize()
    {
        return cache_.length;
    }

    public Sound getAt(int index)
    {
        if (cache_[index] == null)
        {
            SoundDetails soundDetails = soundDetailsMap_.get(index);
            if (soundDetails.getDurationsMs() > 0 || index == 0)
            {
                cache_[index] = new Sound(soundType_, soundDetails.getFrequency(), soundDetails.getAmplitude(),
                        soundDetails.getDurationsMs(), soundDetails.isLeft(), index == 0);
            }
        }

        return cache_[index];
    }

    private static class SoundDetails
    {
        private final double frequency_;
        private final double amplitude_;
        private final int durationsMs_;
        private final boolean isLeft_;

        public SoundDetails(double frequency, double amplitude, int durationsMs, boolean isLeft)
        {
            this.frequency_ = frequency;
            this.amplitude_ = amplitude;
            this.durationsMs_ = durationsMs;
            this.isLeft_ = isLeft;
        }

        public double getFrequency()
        {
            return frequency_;
        }

        public double getAmplitude()
        {
            return amplitude_;
        }

        public int getDurationsMs()
        {
            return durationsMs_;
        }

        public boolean isLeft()
        {
            return isLeft_;
        }
    }
}
