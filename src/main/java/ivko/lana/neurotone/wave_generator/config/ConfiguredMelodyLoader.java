package ivko.lana.neurotone.wave_generator.config;

import ivko.lana.yaml.YamlToJava;

/**
 * Loads melody configuration from YAML files.
 */
public class ConfiguredMelodyLoader
{
    public static final String DEFAULT_CONFIG_PATH = "melodies/jingle_bells.yaml";

    public ConfiguredMelody load()
    {
        return load(DEFAULT_CONFIG_PATH);
    }

    public ConfiguredMelody load(String path)
    {
        ConfiguredMelody melody = YamlToJava.extract(path, ConfiguredMelody.class);
        if (melody == null)
        {
            throw new IllegalStateException(String.format("Failed to load melody config from '%s'", path));
        }
        return melody;
    }
}
