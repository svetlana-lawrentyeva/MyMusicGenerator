package ivko.lana.instruments_for_test.tibetan_sound_analyzer;

import org.tritonus.share.ArraySet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Lana Ivko
 */
public class FrequenciesDependenciesFinder
{
    private List<Integer> frequencies_;
    private Map<Integer, Double> frequenciesToMultiplier_;

    public FrequenciesDependenciesFinder(int delta, int... frequencies)
    {
        if (frequencies == null || frequencies.length < 2)
        {
            throw new IllegalStateException("Need more elements");
        }
        List<Integer> integerList = IntStream.of(frequencies)
                .boxed()
                .collect(Collectors.toList());

        Set<Integer> fr = new HashSet<>();

        for (Integer frequency : integerList)
        {
            for (int i = 0; i <= delta; ++i)
            {
                fr.add(frequency + i);
                fr.add(frequency - i);
            }
        }

        frequencies_ = new ArrayList<>(fr);

        frequenciesToMultiplier_ = new HashMap<>();

        int baseNumber = frequencies[0];
        for (int number : frequencies_)
        {
            frequenciesToMultiplier_.put(number, baseNumber / (double) number);
        }
    }

    public Map<Integer, DependencyDetail> findDependencies()
    {
        Map<Integer, DependencyDetail> frequenciesDependencies= new HashMap<>();
        DependencyDetail maxDependency = new DependencyDetail(Integer.MAX_VALUE, 1);

        for (Integer key : frequenciesToMultiplier_.keySet())
        {
            frequenciesDependencies.put(key, maxDependency);
        }

        int maxNumber = 100;

        for (int i = 1; i <= maxNumber; ++i)
        {
            for (int j = 1; j <= i; ++j)
            {
                DependencyDetail calculatedDependencyDetail = new DependencyDetail(j, i);
                for (int key : frequenciesDependencies.keySet())
                {
                    double originalQuotient = frequenciesToMultiplier_.get(key);

                    DependencyDetail currentDependencyDetail = frequenciesDependencies.get(key);
                    double currentQuotient = currentDependencyDetail.quotient_;
                    double calculatedQuotient = calculatedDependencyDetail.quotient_;

                    double currentDifference = Math.abs(originalQuotient - currentQuotient);
                    double calculatedDifference = Math.abs(originalQuotient - calculatedQuotient);

                    if (calculatedDifference < currentDifference)
                    {
                        frequenciesDependencies.put(key, calculatedDependencyDetail);
                    }
                }
            }
        }
        return frequenciesDependencies;
    }

    public void printOriginal(Map<Integer, Double> originMap)
    {
        for (int key : originMap.keySet())
        {
            System.out.println(String.format("%s - %s", key, originMap.get(key)));
        }
    }

    public void printResult(Map<Integer, DependencyDetail> frequenciesDependencies)
    {
        List<Integer> list = new ArrayList<>(frequenciesDependencies.keySet());
        Collections.sort(list);
        for (int key : list)
        {
            DependencyDetail dependencyDetail = frequenciesDependencies.get(key);
            System.out.println(String.format("%s - %s / %s (%s)", key, dependencyDetail.dividend_, dependencyDetail.divisor_, dependencyDetail.quotient_));
        }
    }

    private static class DependencyDetail
    {
        int dividend_;
        int divisor_;
        double quotient_;

        public DependencyDetail(int dividend, int divisor)
        {
            dividend_ = dividend;
            divisor_ = divisor;
            quotient_ = dividend_ / (double) divisor_;
        }
    }

    public static void main(String[] args)
    {
        FrequenciesDependenciesFinder frequenciesDependenciesFinder = new FrequenciesDependenciesFinder(5, new int[]{274, 286, 294, 308, 319, 332, 343, 358, 368, 382, 413, 429, 447, 463, 483, 502, 521});
        Map<Integer, DependencyDetail> dependencies = frequenciesDependenciesFinder.findDependencies();
        frequenciesDependenciesFinder.printOriginal(frequenciesDependenciesFinder.frequenciesToMultiplier_);
        System.out.println();
        frequenciesDependenciesFinder.printResult(dependencies);
    }
}
