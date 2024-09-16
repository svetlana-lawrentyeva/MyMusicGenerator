package ivko.lana.instruments_for_test.tibetan_sound_analyzer;

/**
 * @author Lana Ivko
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HarmonicFrequencyFinder {
    public static void main(String[] args) {
        double baseFrequency = 274; // Пример базовой частоты
        double[] multipliers = {1, 2.71, 5, 7.73}; // Некратные множители
        double minFrequency = 274; // Минимальная частота диапазона
        double maxFrequency = 525; // Максимальная частота диапазона
        int desiredCount = 12; // Желаемое количество гармоничных частот

        // Генерация гармоничных частот
        List<FrequencyWithMultipliers> harmonicFrequencies = generateHarmonicFrequencies(baseFrequency, multipliers, minFrequency, maxFrequency, desiredCount);

        // Вывод гармоничных частот и их корректированных множителей
        System.out.println("Harmonic Frequencies with Adjusted Multipliers:");
        for (FrequencyWithMultipliers freq : harmonicFrequencies) {
            System.out.printf("%.2f Hz {", freq.frequency);
            for (double multiplier : freq.adjustedMultipliers) {
                System.out.printf("%.2f ", multiplier);
            }
            System.out.println("}");
        }
    }

    // Класс для хранения частоты и скорректированных множителей
    static class FrequencyWithMultipliers {
        double frequency;
        List<Double> adjustedMultipliers;

        FrequencyWithMultipliers(double frequency, List<Double> adjustedMultipliers) {
            this.frequency = frequency;
            this.adjustedMultipliers = adjustedMultipliers;
        }
    }

    public static List<FrequencyWithMultipliers> generateHarmonicFrequencies(double baseFrequency, double[] multipliers, double minFrequency, double maxFrequency, int desiredCount) {
        List<FrequencyWithMultipliers> frequencies = new ArrayList<>();
        Set<Double> uniqueFrequencies = new HashSet<>();

        // Начинаем с базовой частоты
        addFrequencyWithMultipliers(frequencies, baseFrequency, multipliers, uniqueFrequencies);

        // Генерируем частоты на основе корректированных множителей
        for (int i = 0; i < frequencies.size() && frequencies.size() < desiredCount; i++) {
            FrequencyWithMultipliers currentFreq = frequencies.get(i);
            for (double multiplier : multipliers) {
                double newFrequency = currentFreq.frequency * multiplier;
                if (newFrequency >= minFrequency && newFrequency <= maxFrequency && uniqueFrequencies.add(newFrequency)) {
                    List<Double> adjustedMultipliers = adjustMultipliers(newFrequency, multipliers);
                    frequencies.add(new FrequencyWithMultipliers(newFrequency, adjustedMultipliers));
                    if (frequencies.size() >= desiredCount) {
                        break;
                    }
                }
            }
        }

        return frequencies;
    }

    public static void addFrequencyWithMultipliers(List<FrequencyWithMultipliers> frequencies, double frequency, double[] multipliers, Set<Double> uniqueFrequencies) {
        if (uniqueFrequencies.add(frequency)) {
            List<Double> adjustedMultipliers = adjustMultipliers(frequency, multipliers);
            frequencies.add(new FrequencyWithMultipliers(frequency, adjustedMultipliers));
        }
    }

    public static List<Double> adjustMultipliers(double frequency, double[] baseMultipliers) {
        List<Double> adjustedMultipliers = new ArrayList<>();
        for (double multiplier : baseMultipliers) {
            // Корректируем множитель в зависимости от отношения к базовой частоте
            adjustedMultipliers.add(multiplier * (frequency / 274)); // 274 — базовая частота, используется для корректировки
        }
        return adjustedMultipliers;
    }
}





