package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleClearSamplesCreator;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class TibetanHitSamplesCreator
{
    private static final Logger logger = CustomLogger.getLogger(TibetanHitSamplesCreator.class.getName());

    private TibetanClearSamplesCreator clearSamplesCreator = new TibetanClearSamplesCreator();
    public TibetanHitSamplesCreator()
    {
        super();
    }

    public short[] createHitSamples(double frequency, double amplitude)
    {
        frequency *= 1.5;
        amplitude *= 0.1;

        double durationFadeInFactor = 0.025;
        double durationFadeOutFactor = 0.06;
        int duration = (int) (Constants.FADE_IN_DURATION_MS * durationFadeOutFactor);
        duration = Math.max(duration, Constants.PAUSE_DURATION_MS + Constants.SMALL_AMPLITUDES_DURATION_MS + duration);
        clearSamplesCreator.setDurationFadeInFactor(durationFadeInFactor);
        clearSamplesCreator.setDurationFadeOutFactor(durationFadeOutFactor);
        short[] hitSignal = clearSamplesCreator.createClearSamples(duration, frequency, amplitude, true);

        for (double i = 0.5; i < 1; i += 0.5)
        {
            durationFadeOutFactor = i;
            duration = (int) (Constants.FADE_IN_DURATION_MS * durationFadeOutFactor);
            clearSamplesCreator.setDurationFadeOutFactor(durationFadeOutFactor);
            short[] phoneHitSignal = clearSamplesCreator.createClearSamples(duration, frequency, amplitude * (1 - i), true);
            hitSignal = Util.combineSamples(hitSignal, phoneHitSignal);
        }
        return hitSignal;
    }

    private short[] createHitWithMultiplier(double frequency, double amplitude, double durationFadeInFactor, int multiplier)
    {
        double durationFadeOutFactor = 1 + 0.125 * (multiplier - 1);
        int duration = (int) (Constants.FADE_IN_DURATION_MS * durationFadeOutFactor);
        duration = Math.max(duration, Constants.PAUSE_DURATION_MS + Constants.SMALL_AMPLITUDES_DURATION_MS + duration);
        clearSamplesCreator.setDurationFadeInFactor(durationFadeInFactor);
        clearSamplesCreator.setDurationFadeOutFactor(durationFadeOutFactor);
        return clearSamplesCreator.createClearSamples(duration, frequency, amplitude * multiplier, true);
    }
}