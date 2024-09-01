package ivko.lana.util;

import java.util.Map;

/**
 * @author Lana Ivko
 */
public class ChordLibrary
{
    private Map<String, Chord> chordCodes;

    public ChordLibrary()
    {
    }

    public Map<String, Chord> getChordCodes()
    {
        return chordCodes;
    }

    public void setChordCodes(Map<String, Chord> chordCodes)
    {
        this.chordCodes = chordCodes;
    }

    public Chord getChord(String name)
    {
        return chordCodes.get(name);
    }

    @Override
    public String toString()
    {
        return "ChordLibrary{chordCodes=" + chordCodes + '}';
    }
}
