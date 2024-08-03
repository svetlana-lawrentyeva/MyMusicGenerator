package ivko.lana.neurotone.wave_generator.sounds.simple;

import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * @author Lana Ivko
 */
public class SimpleOvertonesHelper implements IOvertoneHelper
{
    private static final double[] HARMONIC_MULTIPLIERS = {1};
    private static final double[] HARMONIC_AMPLITUDES = {1};


    private static final double[] HIT_MULTIPLIERS = {21.46};
    private static final double[] HIT_AMPLITUDES = {4};

    private static final double[] SCALED_HARMONIC_AMPLITUDES = {
            HARMONIC_AMPLITUDES[0] * HARMONIC_AMPLITUDE_SCALE_FACTOR
    };

    private static final double[] SCALED_HIT_AMPLITUDES = {
            HIT_AMPLITUDES[0] * HIT_AMPLITUDE_SCALE_FACTOR
    };

    @Override
    public double[] getMultipliers()
    {
        return HARMONIC_MULTIPLIERS;
    }

    @Override
    public double[] getAmplitudes()
    {
        return HARMONIC_AMPLITUDES;
    }

    @Override
    public double[] getHitMultipliers()
    {
        return HIT_MULTIPLIERS;
    }

    @Override
    public double[] getHitAmplitudes()
    {
        return HIT_AMPLITUDES;
    }
}
