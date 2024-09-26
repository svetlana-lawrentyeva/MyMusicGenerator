package ivko.lana.neurotone.util;

import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * @author Lana Ivko
 */
public class ShiftAddFactor implements IShiftFactor
{
    private final double add_;
    private final double amplitude_;
    private final double phaseMultiplier_;

    public ShiftAddFactor(double add, double amplitude, double phaseMultiplier)
    {
        add_ = add;
        amplitude_ = amplitude;
        phaseMultiplier_ = phaseMultiplier;
    }

    @Override
    public double calculate(double value)
    {
        return value + add_;
    }

    @Override
    public double getAmplitude()
    {
        return amplitude_ * IOvertoneHelper.GAIN_MULTIPLIER;
    }

    @Override
    public double getPhaseMultiplier()
    {
        return phaseMultiplier_;
    }
}
