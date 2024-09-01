package ivko.lana.yaml;

import java.util.List;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class Frequencies
{
    private Map<Integer, List<Double>> frequencies;

    public Map<Integer, List<Double>> getFrequencies()
    {
        return frequencies;
    }

    public void setFrequencies(Map<Integer, List<Double>> frequencies)
    {
        this.frequencies = frequencies;
    }
}
