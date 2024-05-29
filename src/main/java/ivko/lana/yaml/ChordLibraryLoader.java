package ivko.lana.yaml;

import ivko.lana.util.ChordLibrary;

/**
 * @author Lana Ivko
 */
public class ChordLibraryLoader
{
    public static final String FILE_NAME = "chordFinder/chordCodes.yaml";

    public static ChordLibrary load()
    {
        return YamlToJava.extract(FILE_NAME, ChordLibrary.class);
    }
}
