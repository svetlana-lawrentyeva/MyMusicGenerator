package ivko.lana.neurotone.wave_generator.sounds.violin;

import ivko.lana.neurotone.util.IShiftFactor;
import ivko.lana.neurotone.util.ShiftAddFactor;
import ivko.lana.neurotone.util.ShiftMultipleFactor;
import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * @author Lana Ivko
 */
public class ViolinOvertonesHelper implements IOvertoneHelper
{
    private static final IShiftFactor[] SIMPLE_SHIFT_FACTORS =
            {
                    new ShiftAddFactor(-1.0, 0.5, 1),
                    new ShiftAddFactor(0.0, 0.5, 1),
                    new ShiftAddFactor(1.0, 0.5, 1),
            };

    @Override
    public IShiftFactor[] getHarmonyShiftFactors()
    {
        return SIMPLE_SHIFT_FACTORS;
    }

    @Override
    public IShiftFactor[] getHitShiftFactors()
    {
        return SIMPLE_SHIFT_FACTORS;
    }
}
