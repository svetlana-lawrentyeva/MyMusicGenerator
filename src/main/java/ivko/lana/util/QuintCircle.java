package ivko.lana.util;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class QuintCircle
{
    private List<Pair> chordPairs;

    public QuintCircle()
    {
    }

    public List<Pair> getChordPairs()
    {
        return chordPairs;
    }

    @Override
    public String toString()
    {
        return "ChordPairLibrary{chordPairs=" + chordPairs + '}';
    }
}
