package ivko.lana.yaml;

import java.util.Map;

/**
 * @author Lana Ivko
 */
public class NextNoteProbabilities
{
    private Map<Integer, Map<Integer, Double>> probabilitiesByPreviousNote;

    public Map<Integer, Double> getProbabilitiesByPreviousNote(int previousNote)
    {
        return probabilitiesByPreviousNote.get(previousNote);
    }

    public void setProbabilitiesByPreviousNote(Map<Integer, Map<Integer, Double>> probabilitiesByPreviousNote)
    {
        this.probabilitiesByPreviousNote = probabilitiesByPreviousNote;
    }
}
