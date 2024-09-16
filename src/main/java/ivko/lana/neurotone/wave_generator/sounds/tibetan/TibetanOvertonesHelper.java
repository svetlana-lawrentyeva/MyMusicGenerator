package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.util.ShiftFactor;
import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Lana Ivko
 */
public class TibetanOvertonesHelper implements IOvertoneHelper
{
    private static final ShiftFactor[] HARMONY_SHIFT_FACTORS =
            {
                    new ShiftFactor(1, 1, 1.5, 1),
                    new ShiftFactor(10, 4, 0.025, 1.25),
                    new ShiftFactor(9, 2, 0.0015, 1.5),
                    new ShiftFactor(8, 1, 0.00225, 1.75)


//                    new ShiftFactor(1, 1, 1, 4),
//                    new ShiftFactor(9, 4, 0.025, 2),
//                    new ShiftFactor(8, 2, 0.0015, 0.25),
//                    new ShiftFactor(6, 1, 0.001, 0.125)
            };
    private static final ShiftFactor[] HIT_SHIFT_FACTORS = generateHitShiftFactors();

    private static ShiftFactor[] generateHitShiftFactors()
    {
        Map<Integer, Integer> shiftByCounter = new TreeMap<>();
        shiftByCounter.put(3, 3);
        shiftByCounter.put(5, 3);
        shiftByCounter.put(6, 4);
        shiftByCounter.put(7, 4);
        shiftByCounter.put(8, 5);
        shiftByCounter.put(9, 5);
        shiftByCounter.put(10, 6);
        shiftByCounter.put(11, 7);

        int totalCounter = shiftByCounter.values().stream().mapToInt(i -> i).sum();
        ShiftFactor[] shiftFactors = new ShiftFactor[totalCounter];

        int value = 10;
        Iterator<Map.Entry<Integer, Integer>> iterator = shiftByCounter.entrySet().iterator();

        int maxValueIndex = (int) (totalCounter * 0.1);   // Центр пика
        double sigma = 0.015;   // Параметр, управляющий шириной пика
        double maxAmplitude = 0.0125;

        Integer shift = 0;
        Integer counter = 0;
        int shiftFactorCounter = 0;
        int divider = 4;
        int phaseWidth = 5;

        while (iterator.hasNext())
        {
            Map.Entry<Integer, Integer> shiftToCounter = iterator.next();
            shift = shiftToCounter.getKey();
            counter = shiftToCounter.getValue();
            for (int i = 0; i < counter; ++i)
            {
                shiftFactors[shiftFactorCounter++] = new ShiftFactor(value, divider, getAmplitude(shiftFactorCounter, maxValueIndex, maxAmplitude, sigma, i), getPhaseMultiplier(phaseWidth, value));
                value += shift;
            }
        }
        return shiftFactors;
    }

    private static int getPhaseMultiplier(int width, int value)
    {
        int cycleLength = 2 * width - 1; // Полная длина цикла (например, 9 для ширины 5)
        int positionInCycle = (value - 1) % cycleLength; // Позиция внутри цикла (индекс)

        // Если позиция меньше ширины, то поднимаемся к максимальному значению
        if (positionInCycle < width)
        {
            return positionInCycle + 1;
        }
        // Иначе спускаемся обратно
        else
        {
            return 2 * width - (positionInCycle + 1);
        }
    }

    public static double getAmplitude(double value, double maxValue, double maxAmplitude, double sigma, int index)
    {
        return (maxAmplitude * Math.exp(-Math.pow(value - maxValue, 1) / 100000.0)) * Math.pow(sigma, index );
    }

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
