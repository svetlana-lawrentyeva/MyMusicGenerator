package ivko.lana.yaml;

import ivko.lana.util.QuintCircle;

/**
 * @author Lana Ivko
 */
public class QuintCircleLoader
{
    public static final String FILE_NAME = "chordFinder/quintCircle.yaml";

    public static QuintCircle load()
    {
        return YamlToJava.extract(FILE_NAME, QuintCircle.class);
    }
}
