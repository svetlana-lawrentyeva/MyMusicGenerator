package ivko.lana.neurotone.wave_generator.melody;

import ivko.lana.neurotone.processing.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class NoteGenerator
{
    private static final Map<Integer, Factor> MAJOR_SCALE_DEGREE_FACTOR = new HashMap<>();
    static
    {
//        MAJOR_SCALE_DEGREE_FACTOR.put(1, new Factor(Math.pow(2, 0.0 / 12.0))); // 1-я ступень
//        MAJOR_SCALE_DEGREE_FACTOR.put(2, new Factor(Math.pow(2, 2.0 / 12.0))); // 2-я ступень
//        MAJOR_SCALE_DEGREE_FACTOR.put(3, new Factor(Math.pow(2, 4.0 / 12.0))); // 3-я ступень
//        MAJOR_SCALE_DEGREE_FACTOR.put(4, new Factor(Math.pow(2, 5.0 / 12.0))); // 4-я ступень
//        MAJOR_SCALE_DEGREE_FACTOR.put(5, new Factor(Math.pow(2, 7.0 / 12.0))); // 5-я ступень
//        MAJOR_SCALE_DEGREE_FACTOR.put(6, new Factor(Math.pow(2, 9.0 / 12.0))); // 6-я ступень
//        MAJOR_SCALE_DEGREE_FACTOR.put(7, new Factor(Math.pow(2, 11.0 / 12.0))); // 7-я ступень


        MAJOR_SCALE_DEGREE_FACTOR.put(1, new Factor(1, 1));
        MAJOR_SCALE_DEGREE_FACTOR.put(2, new Factor(9, 8));
        MAJOR_SCALE_DEGREE_FACTOR.put(3, new Factor(5, 4));
        MAJOR_SCALE_DEGREE_FACTOR.put(4, new Factor(4, 3));
        MAJOR_SCALE_DEGREE_FACTOR.put(5, new Factor(3, 2));
        MAJOR_SCALE_DEGREE_FACTOR.put(6, new Factor(5, 3));
        MAJOR_SCALE_DEGREE_FACTOR.put(7, new Factor(15, 8));
    }
    private static final Map<Integer, Factor> MINOR_SCALE_DEGREE_FACTOR = new HashMap<>();
    static
    {
//        MINOR_SCALE_DEGREE_FACTOR.put(1, new Factor(Math.pow(2, 0.0 / 12.0))); // 1-я ступень
//        MINOR_SCALE_DEGREE_FACTOR.put(2, new Factor(Math.pow(2, 2.0 / 12.0))); // 2-я ступень
//        MINOR_SCALE_DEGREE_FACTOR.put(3, new Factor(Math.pow(2, 3.0 / 12.0))); // 3-я ступень (минорная терция)
//        MINOR_SCALE_DEGREE_FACTOR.put(4, new Factor(Math.pow(2, 5.0 / 12.0))); // 4-я ступень
//        MINOR_SCALE_DEGREE_FACTOR.put(5, new Factor(Math.pow(2, 7.0 / 12.0))); // 5-я ступень
//        MINOR_SCALE_DEGREE_FACTOR.put(6, new Factor(Math.pow(2, 8.0 / 12.0))); // 6-я ступень (минорная секста)
//        MINOR_SCALE_DEGREE_FACTOR.put(7, new Factor(Math.pow(2, 10.0 / 12.0))); // 7-я ступень (минорная септима)


        MINOR_SCALE_DEGREE_FACTOR.put(1, new Factor(1, 1));
        MINOR_SCALE_DEGREE_FACTOR.put(2, new Factor(9, 8));
        MINOR_SCALE_DEGREE_FACTOR.put(3, new Factor(6, 5));
        MINOR_SCALE_DEGREE_FACTOR.put(4, new Factor(4, 3));
        MINOR_SCALE_DEGREE_FACTOR.put(5, new Factor(3, 2));
        MINOR_SCALE_DEGREE_FACTOR.put(6, new Factor(8, 5));
        MINOR_SCALE_DEGREE_FACTOR.put(7, new Factor(9, 5));
    }

    public static double getFrequency(int degree)
    {
        return getScaleDegreeFactor().get(degree).getFrequency(Constants.BaseFrequency_);
    }

    private static Map<Integer, Factor> getScaleDegreeFactor()
    {
        return Constants.ScaleDegreeType_.getFactorMap();
    }

    private static class Factor
    {
        private double factor_;

        Factor(double factor)
        {
            factor_ = factor;
        }


        Factor(int multiplier, int divider)
        {
            factor_ = multiplier / (double) divider;
        }

        double getFrequency(double scaleDegree)
        {
            return scaleDegree * factor_;
        }
    }

    public enum ScaleDegreeType
    {
        MAJOR {@Override public Map<Integer, Factor> getFactorMap() {return MAJOR_SCALE_DEGREE_FACTOR;}},
        MINOR {@Override public Map<Integer, Factor> getFactorMap() {return MINOR_SCALE_DEGREE_FACTOR;}};

        public abstract Map<Integer, Factor> getFactorMap();
    }
}
