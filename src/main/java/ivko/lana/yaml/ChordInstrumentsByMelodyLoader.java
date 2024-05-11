package ivko.lana.yaml;

import java.io.File;

/**
 * @author Lana Ivko
 */
public class ChordInstrumentsByMelodyLoader
{
    public static final String FILE_NAME = "chordFinder/chordInstrumentFinder.yaml";
    public static ChordInstrumentsByMelody load()
    {
        return YamlToJava.extract(FILE_NAME, ChordInstrumentsByMelody.class);
    }
}
