package ivko.lana.neurotone.wave_generator.sounds;

import ivko.lana.neurotone.util.IShiftFactor;

/**
 * @author Lana Ivko
 */
public interface IOvertoneHelper
{
    double GAIN_MULTIPLIER = 0.5;

    IShiftFactor[] getHarmonyShiftFactors();
    IShiftFactor[] getHitShiftFactors();
}
