package ivko.lana.neurotone.wave_generator.sounds.mixed_sound;

import ivko.lana.neurotone.util.ShiftFactor;
import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * Helper that blends a small set of harmonic and inharmonic partials so that "mixed" sounds
 * can share overtone definitions with the rest of the generator pipeline.
 */
public class MixedSoundHelper implements IOvertoneHelper
{
    private static final ShiftFactor[] HARMONY_SHIFT_FACTORS =
            {
                    // Fundamental
                    new ShiftFactor(1, 1, 1.25, 1),
                    // Low inharmonic
                    new ShiftFactor(5, 3, 0.12, 1.5),
                    new ShiftFactor(7, 4, 0.08, 1.75),
                    // Subtle high shimmer
                    new ShiftFactor(12, 5, 0.04, 2)
            };

    private static final ShiftFactor[] HIT_SHIFT_FACTORS = HARMONY_SHIFT_FACTORS;

    @Override
    public ShiftFactor[] getHarmonyShiftFactors()
    {
        return HARMONY_SHIFT_FACTORS;
    }

    @Override
    public ShiftFactor[] getHitShiftFactors()
    {
        return HIT_SHIFT_FACTORS;
    }
}
