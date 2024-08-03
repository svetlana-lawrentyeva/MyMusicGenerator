package ivko.lana.yaml;

import ivko.lana.musicentities.InstrumentCodesConfig;

/**
 * @author Lana Ivko
 */
public class InstrumentCodesConfigLoader
{
    public static final String FILE_NAME = "scales/instrumentCodes.yaml";

    public static InstrumentCodesConfig load()
    {
        return YamlToJava.extract(FILE_NAME, InstrumentCodesConfig.class);
    }
}
