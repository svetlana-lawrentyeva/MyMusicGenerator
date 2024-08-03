package ivko.lana.yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lana Ivko
 */
public class ChordInstrumentsByMelody
{
    private Map<Integer, List<Integer>> chordInstrumentsByMelody;

    public ChordInstrumentsByMelody(Map<Integer, List<Integer>> chordInstrumentsByMelody)
    {
        this.chordInstrumentsByMelody = chordInstrumentsByMelody;
    }

    public ChordInstrumentsByMelody()
    {
        chordInstrumentsByMelody = new HashMap<>();
    }

    public void addInstrument(Integer key, List<Integer> instrumentProgramNumbers)
    {
        chordInstrumentsByMelody.put(key, instrumentProgramNumbers);
    }

    public void setChordInstrumentsByMelody(Map<Integer, List<Integer>> chordInstrumentsByMelody)
    {
        this.chordInstrumentsByMelody = chordInstrumentsByMelody;
    }

    public List<Integer> getChordInstruments(Integer key)
    {
        return chordInstrumentsByMelody.get(key);
    }

    public Set<Integer> getMelodyInstruments()
    {
        return chordInstrumentsByMelody.keySet();
    }
}
