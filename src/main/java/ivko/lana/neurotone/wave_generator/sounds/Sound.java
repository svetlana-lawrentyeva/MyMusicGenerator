package ivko.lana.neurotone.wave_generator.sounds;

import ivko.lana.neurotone.util.CustomLogger;

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

    public Sound(SoundType soundType, double frequency, double amplitude, int durationMs, boolean isLeft, boolean isBaseFrequency)
    {
        frequency_ = frequency;
        durationMs_ = durationMs;
        ISamplesCreator samplesCreator = soundType.getSamplesCreator();
        samples_ = samplesCreator.createSamples(durationMs, frequency, amplitude, isLeft, isBaseFrequency);
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
