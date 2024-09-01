package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.processing.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class NoteGenerator
{
    private static final Map<Integer, Factor> SCALE_DEGREE_FACTOR = new HashMap<>();
    static
    {
        SCALE_DEGREE_FACTOR.put(1, new Factor(1, 1));
        SCALE_DEGREE_FACTOR.put(2, new Factor(9, 8));
        SCALE_DEGREE_FACTOR.put(3, new Factor(5, 4));
        SCALE_DEGREE_FACTOR.put(4, new Factor(4, 3));
        SCALE_DEGREE_FACTOR.put(5, new Factor(3, 2));
        SCALE_DEGREE_FACTOR.put(6, new Factor(5, 3));
        SCALE_DEGREE_FACTOR.put(7, new Factor(15, 8));
    }

    public static double getScaleDegree(int degree)
    {
        return SCALE_DEGREE_FACTOR.get(degree).getFactor(Constants.BaseFrequency_);
    }

    private static class Factor
    {
        private final int multiplier_;
        private final int divider_;

        Factor(int multiplier, int divider)
        {
            multiplier_ = multiplier;
            divider_ = divider;
        }

        double getFactor(double frequency)
        {
            return frequency * multiplier_ / divider_;
        }
    }
}
