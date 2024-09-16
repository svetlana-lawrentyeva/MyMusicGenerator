package ivko.lana.instruments_for_test.tibetan_sound_analyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class FrequenciesArray
{
    private static final double[] HARMONIC_MULTIPLIERS = {1, 2.71, 5, 7.73};

    private int minLimit_;
    private int maxLimit_;
    private int elementsNumber_;

    public FrequenciesArray(double baseFrequency, int minLimit, int maxLimit, int elementsNumber)
    {
        minLimit_ = minLimit;
        maxLimit_ = maxLimit;
        elementsNumber_ = elementsNumber;

        List<Double> result = new ArrayList<>();
        result.add(baseFrequency);

        for (double multiplier : HARMONIC_MULTIPLIERS)
        {
            double potentialValue = multiplier * baseFrequency;
            if (potentialValue >= minLimit && potentialValue <= maxLimit)
            {
                result.add(potentialValue);
            }
        }

        if (result.size() < elementsNumber)
        {

        }

    }

    private void process(double frequency, List<Double> result)
    {
        result.add(frequency);
        for (double multiplier : HARMONIC_MULTIPLIERS)
        {
            double potentialValue = multiplier * frequency;
            if (potentialValue >= minLimit_ && potentialValue <= maxLimit_)
            {
                result.add(potentialValue);
            }
        }

        if (result.size() < elementsNumber_)
        {

        }
    }

    private double[] getFrequencies()
    {
        return new double[0];
    }


    public static List<Double> generateHarmonicFrequencies(double baseFrequency)
    {
        List<Double> frequencies = new ArrayList<>();

        // Добавление базовой частоты и её гармоник
        for (double multiplier : HARMONIC_MULTIPLIERS)
        {
            frequencies.add(baseFrequency * multiplier);
        }

        // Генерация дополнительных гармоничных частот
        // В данном случае просто продублируем цикл для примера, можно добавить свои алгоритмы генерации
        double[] additionalMultipliers = {0.5, 1.5, 2.5}; // Пример дополнительных множителей
        for (double multiplier : additionalMultipliers)
        {
            frequencies.add(baseFrequency * multiplier);
        }

        return frequencies;
    }

    public static void main(String[] args)
    {
//        FrequenciesArray frequenciesArray = new FrequenciesArray(271, 75, 750, 17);
//        double[] frequencies = frequenciesArray.getFrequencies();
//        System.out.println(frequencies);

        double baseFrequency = 271; // Пример базовой частоты (например, нота A4)

        // Создание списка гармоничных частот
        List<Double> harmonicFrequencies = generateHarmonicFrequencies(baseFrequency);

        // Вывод гармоничных частот
        for (double frequency : harmonicFrequencies)
        {
            System.out.printf("%.2f\n", frequency);
        }
    }
}
