package ivko.lana.neurotone.util;

import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

/**
 * @author Lana Ivko
 */
public class ShiftMultipleFactor implements IShiftFactor
{
    private final int multiplier_;
    private final double divider_;
    private final double amplitude_;
    private final double phaseMultiplier_;

    public ShiftMultipleFactor(int multiplier, int divider, double amplitude, double phaseMultiplier)
    {
        multiplier_ = multiplier;
        divider_ = divider;
        amplitude_ = amplitude;
        phaseMultiplier_ = phaseMultiplier;
    }

    @Override
    public double calculate(double value)
    {
        return value * multiplier_ / divider_;
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
