package ivko.lana.neurotone.wave_generator.sounds;

import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.wave_generator.SoundsCache;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class Sound
{
    private static final Logger logger = CustomLogger.getLogger(Sound.class.getName());

    private final double frequency_;
    private final int durationMs_;
    private final short[] samples_;

    public Sound(SoundType soundType, SoundsCache.SoundDetails soundDetails, int overtoneIndex)
    {
        frequency_ = soundDetails.getFrequency();
        durationMs_ = soundDetails.getDurationsMs();
        double amplitude = soundDetails.getAmplitude();
        boolean isLeft = soundDetails.isLeft();
        double phaseMultiplier = soundDetails.getPhaseMultiplier();
        ISamplesCreator samplesCreator = soundType.getSamplesCreator();
        samples_ = samplesCreator.createSamples(durationMs_, frequency_, amplitude, isLeft, phaseMultiplier, overtoneIndex);
    }

    public double getFrequency()
    {
        return frequency_;
    }

    public int getDurationMs()
    {
        return durationMs_;
    }

    public short[] getSamples()
    {
        return samples_;
    }
}
