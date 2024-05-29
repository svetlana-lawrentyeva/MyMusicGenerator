package ivko.lana.yaml;

import ivko.lana.generators.Initializer;

/**
 * @author Lana Ivko
 */
public class MusicScaleConfigLoader
{
    public static final String FILE_NAME = Initializer.isTest() ? "scalesTest/scales.yaml" : "scales/scales.yaml";
    public static MusicScalesConfig load()
    {
        return YamlToJava.extract(FILE_NAME, MusicScalesConfig.class);
    }
}
