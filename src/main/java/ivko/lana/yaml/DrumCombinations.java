package ivko.lana.yaml;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class DrumCombinations
{
    private List<List<Integer>> combinations;

    public DrumCombinations(){}

    public List<List<Integer>> getCombinations()
    {
        return combinations;
    }

    public void setCombinations(List<List<Integer>> combinations)
    {
        this.combinations = combinations;
    }
}
