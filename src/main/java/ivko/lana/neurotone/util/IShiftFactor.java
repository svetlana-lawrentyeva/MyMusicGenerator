package ivko.lana.neurotone.util;

/**
 * @author Lana Ivko
 */
public interface IShiftFactor
{
    double calculate(double value);
    double getAmplitude();
    double getPhaseMultiplier();
}
