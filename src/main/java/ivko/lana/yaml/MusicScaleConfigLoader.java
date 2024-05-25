package ivko.lana.yaml;

/**
 * @author Lana Ivko
 */
public class MusicScaleConfigLoader
{
    public static final String FILE_NAME = "scalesTest/scales.yaml";
    public static MusicScalesConfig load()
    {
        return YamlToJava.extract(FILE_NAME, MusicScalesConfig.class);
    }
}
