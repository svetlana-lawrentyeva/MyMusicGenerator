package ivko.lana.frequencycombination;

/**
 * @author Lana Ivko
 */
import java.util.ArrayList;
import java.util.List;

public class FrequencyManager {
    private List<Frequency> frequencies;

    public FrequencyManager() {
        this.frequencies = new ArrayList<>();
    }

    public void setFrequencies(List<Frequency> frequencies)
    {
        this.frequencies = frequencies;
    }

    public void addFrequency(Frequency frequency) {
        frequencies.add(frequency);
    }

    public List<Frequency> getFrequencies() {
        return frequencies;
    }

    public Frequency getFrequencyByValue(double value) {
        for (Frequency frequency : frequencies) {
            if (Double.compare(frequency.getValue(), value) == 0) {
                return frequency;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "FrequencyManager{" +
                "frequencies=" + frequencies +
                '}';
    }
}
