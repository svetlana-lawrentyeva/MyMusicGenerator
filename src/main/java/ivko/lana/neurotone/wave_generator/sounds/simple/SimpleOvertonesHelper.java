package ivko.lana.neurotone.wave_generator.sounds.simple;

import ivko.lana.neurotone.util.ShiftFactor;
import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * @author Lana Ivko
 */
public class SimpleOvertonesHelper implements IOvertoneHelper
{
    private static final ShiftFactor[] SIMPLE_SHIFT_FACTORS =
            {
                    new ShiftFactor(1, 1, 1.5, 1)
            };

    @Override
    public ShiftFactor[] getHarmonyShiftFactors()
    {
        return SIMPLE_SHIFT_FACTORS;
    }

    @Override
    public ShiftFactor[] getHitShiftFactors()
    {
        return SIMPLE_SHIFT_FACTORS;
    }
}
