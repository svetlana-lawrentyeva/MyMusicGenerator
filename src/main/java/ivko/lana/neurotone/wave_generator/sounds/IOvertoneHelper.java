package ivko.lana.neurotone.wave_generator.sounds;

/**
 * @author Lana Ivko
 */
public interface IOvertoneHelper
{
    int GAIN_MULTIPLIER = 1;
    double HARMONIC_AMPLITUDE_SCALE_FACTOR = 0.05 * GAIN_MULTIPLIER; // Фактор уменьшения амплитуды обертонов
    double HIT_AMPLITUDE_SCALE_FACTOR = 0.003 * GAIN_MULTIPLIER; // Фактор уменьшения амплитуды удара

    double[] getMultipliers();
    double[] getAmplitudes();
    double[] getHitMultipliers();
    double[] getHitAmplitudes();
}
