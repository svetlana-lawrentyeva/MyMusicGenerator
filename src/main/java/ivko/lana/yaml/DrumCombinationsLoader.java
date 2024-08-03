package ivko.lana.yaml;

/**
 * @author Lana Ivko
 */
public class DrumCombinationsLoader
{
    public static final String FILE_NAME = "drum_combinations/drums.yaml";

    public static DrumCombinations load()
    {
        return YamlToJava.extract(FILE_NAME, DrumCombinations.class);
    }
}
