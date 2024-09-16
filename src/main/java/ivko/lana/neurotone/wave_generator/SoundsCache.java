package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.ShiftFactor;
import ivko.lana.neurotone.wave_generator.melody.NoteGenerator;
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
    private final Map<Integer, Sound> soundsByOvertones_;

    public SoundsCache(WaveType waveType, SoundType soundType, double scaleDegree, int durationMs, boolean isLeft)
    {
        IOvertoneHelper overtoneHelper = soundType.getOvertoneHelper();
        ShiftFactor[] harmonyShiftFactors = overtoneHelper.getHarmonyShiftFactors();
        soundsByOvertones_ = new HashMap<>();

        for (int i = 0; i < harmonyShiftFactors.length; ++i)
        {
            ShiftFactor shiftFactor = harmonyShiftFactors[i];
            double baseFrequency = scaleDegree == 0 ? 0 : waveType.getNoteDistributor().getFrequency((int) scaleDegree);
            double overtoneFrequency = shiftFactor.calculate(baseFrequency);
            double amplitude = shiftFactor.getAmplitude();
            double phaseMultiplier = shiftFactor.getPhaseMultiplier();

            overtoneFrequency = isLeft ? overtoneFrequency : overtoneFrequency + Constants.FrequencyOffset_;
            SoundDetails soundDetails = new SoundDetails(overtoneFrequency, amplitude, durationMs, isLeft, phaseMultiplier);
            soundsByOvertones_.put(i, new Sound(soundType, soundDetails, i));
        }
    }

    public int getSize()
    {
        return soundsByOvertones_.size();
    }

    public Sound getAt(int index)
    {
        return soundsByOvertones_.get(index);
    }

    public static class SoundDetails
    {
        private final double frequency_;
        private final double amplitude_;
        private final int durationsMs_;
        private final boolean isLeft_;
        private final double phaseMultiplier;

        public SoundDetails(double frequency, double amplitude, int durationsMs, boolean isLeft, double phaseMultiplier)
        {
            frequency_ = frequency;
            amplitude_ = amplitude;
            durationsMs_ = durationsMs;
            isLeft_ = isLeft;
            this.phaseMultiplier = phaseMultiplier;
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

        public double getPhaseMultiplier()
        {
            return phaseMultiplier;
        }
    }
}
