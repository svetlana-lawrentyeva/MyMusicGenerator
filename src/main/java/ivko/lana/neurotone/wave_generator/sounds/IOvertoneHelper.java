package ivko.lana.neurotone.wave_generator.sounds;

import ivko.lana.neurotone.util.ShiftFactor;

/**
 * @author Lana Ivko
 */
public interface IOvertoneHelper
{
    double GAIN_MULTIPLIER = 0.2;

    ShiftFactor[] getHarmonyShiftFactors();
    ShiftFactor[] getHitShiftFactors();
}
