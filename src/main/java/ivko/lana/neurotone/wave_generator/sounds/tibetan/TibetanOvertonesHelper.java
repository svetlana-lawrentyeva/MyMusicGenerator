package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * @author Lana Ivko
 */
public class TibetanOvertonesHelper implements IOvertoneHelper
{
    private static final double[] HARMONIC_MULTIPLIERS = {1, 2.54, 4.59, 7.15};
    //    private static final double[] HARMONIC_MULTIPLIERS = {1, 2.8125, 5, 7.85};
    private static final double[] HARMONIC_AMPLITUDES = {1, 1, 0.0005, 0.000025};
//    private static final double[] HARMONIC_AMPLITUDES = {0.5, 0.35, 0.17, 0.14};


    private static final double[] HIT_MULTIPLIERS = {3.125, 19, 21.46, 24, 31.15, 35.53, 37.75, 41.74, 43.98};
    private static final double[] HIT_AMPLITUDES = {0.25, 1, 4, 0.9, 1, 2, 3, 4, 5};
    private static final double[] OVERTONE_MULTIPLIERS = {1};
    private static final double[] OVERTONE_AMPLITUDES = {1};

    // SINCE_1
//    private static final double[] HARMONIC_MULTIPLIERS = {1, 2.88, 4.49, 6.96};
//    private static final double[] HARMONIC_AMPLITUDES = {0.5, 0.35, 0.17, 0.14};

    // OIGINAL
//    private static final double[] HARMONIC_MULTIPLIERS = {1, 2.7, 4.8, 7.5};
//    private static final double[] HARMONIC_AMPLITUDES = {0.5, 0.2, 0.05, 0.02};

    // FREQUENCY_BASED
//    private static final double [] OVERTONE_MULTIPLIERS = {1, 2, 4, 8, 16, 32};
//    private static final double[] OVERTONE_AMPLITUDES = {1, 0.75, 0.5, 0.45, 0.475, 0.325};

    private static final double[] SCALED_HARMONIC_AMPLITUDES = {
            HARMONIC_AMPLITUDES[0] * HARMONIC_AMPLITUDE_SCALE_FACTOR,
            HARMONIC_AMPLITUDES[1] * HARMONIC_AMPLITUDE_SCALE_FACTOR,
            HARMONIC_AMPLITUDES[2] * HARMONIC_AMPLITUDE_SCALE_FACTOR,
            HARMONIC_AMPLITUDES[3] * HARMONIC_AMPLITUDE_SCALE_FACTOR
    };

    private static final double[] SCALED_HIT_AMPLITUDES = {
            HIT_AMPLITUDES[0] * HIT_AMPLITUDE_SCALE_FACTOR,
            HIT_AMPLITUDES[1] * HIT_AMPLITUDE_SCALE_FACTOR,
            HIT_AMPLITUDES[2] * HIT_AMPLITUDE_SCALE_FACTOR,
            HIT_AMPLITUDES[3] * HIT_AMPLITUDE_SCALE_FACTOR,
            HIT_AMPLITUDES[4] * HIT_AMPLITUDE_SCALE_FACTOR,
            HIT_AMPLITUDES[5] * HIT_AMPLITUDE_SCALE_FACTOR,
            HIT_AMPLITUDES[6] * HIT_AMPLITUDE_SCALE_FACTOR,
            HIT_AMPLITUDES[7] * HIT_AMPLITUDE_SCALE_FACTOR,
            HIT_AMPLITUDES[8] * HIT_AMPLITUDE_SCALE_FACTOR
    };

    public static final double[] MULTIPLIERS = new double[HARMONIC_MULTIPLIERS.length * OVERTONE_MULTIPLIERS.length];
    public static final double[] AMPLITUDES = new double[HARMONIC_AMPLITUDES.length * OVERTONE_AMPLITUDES.length];

    static
    {
        int currentIndex = 0;
        for (int i = 0; i < HARMONIC_MULTIPLIERS.length; ++i)
        {
            double harmonica = HARMONIC_MULTIPLIERS[i];
            double harmonicAmplitude = SCALED_HARMONIC_AMPLITUDES[i];

            for (int j = 0; j < OVERTONE_MULTIPLIERS.length; ++j)
            {
                double overtone = OVERTONE_MULTIPLIERS[j];
                double overtoneAmplitude = OVERTONE_AMPLITUDES[j];

                double resultMultiplier = harmonica * overtone;
                double resultAmplitude = harmonicAmplitude * overtoneAmplitude;
                MULTIPLIERS[currentIndex] = resultMultiplier;
                AMPLITUDES[currentIndex++] = resultAmplitude;
            }
        }
    }

    @Override
    public double[] getMultipliers()
    {
        return MULTIPLIERS;
    }

    @Override
    public double[] getAmplitudes()
    {
        return AMPLITUDES;
    }

    @Override
    public double[] getHitMultipliers()
    {
        return HIT_MULTIPLIERS;
    }

    @Override
    public double[] getHitAmplitudes()
    {
        return SCALED_HIT_AMPLITUDES;
    }
}
