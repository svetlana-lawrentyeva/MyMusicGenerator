package ivko.lana.neurotone.wave_generator.sounds.simple;

import ivko.lana.neurotone.util.ShiftMultipleFactor;
import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * @author Lana Ivko
 */
public class SimpleOvertonesHelper implements IOvertoneHelper
{
    private static final ShiftMultipleFactor[] SIMPLE_SHIFT_FACTORS =
            {
                    new ShiftMultipleFactor(1, 1, 1.5, 1)
            };

    @Override
    public ShiftMultipleFactor[] getHarmonyShiftFactors()
    {
        return SIMPLE_SHIFT_FACTORS;
    }

    @Override
    public ShiftMultipleFactor[] getHitShiftFactors()
    {
        return SIMPLE_SHIFT_FACTORS;
    }
}
