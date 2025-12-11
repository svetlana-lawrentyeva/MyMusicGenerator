package ivko.lana.neurotone.wave_generator.sounds.mixed_sound;

import ivko.lana.neurotone.util.ShiftMultipleFactor;
import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * Helper that blends a small set of harmonic and inharmonic partials so that "mixed" sounds
 * can share overtone definitions with the rest of the generator pipeline.
 */
public class MixedSoundHelper implements IOvertoneHelper
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
