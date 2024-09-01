package ivko.lana.yaml;

/**
 * @author Lana Ivko
 */

public class FrequencyLoader
{
    public static final String FILE_NAME = "tibetan/just_notation.yaml";
//    public static final String FILE_NAME = "tibetan/pifagor.yaml";
//    public static final String FILE_NAME = "tibetan/tempiration.yaml";

    public static Frequencies load()
    {
        return YamlToJava.extract(FILE_NAME, Frequencies.class);
    }
}

