package ivko.lana.yaml;

import ivko.lana.frequencycombination.FrequencyManager;

/**
 * @author Lana Ivko
 */
public class FrequencyCombinationLoader
{
    public static final String FREQUENCY_FILE_NAME = "frequency_generator/frequency.yaml";
    public static final String BINAURAL_FILE_NAME = "frequency_generator/binaural.yaml";
    public static FrequencyManager load(FrequencyType frequencyType)
    {
        String fileName = frequencyType == FrequencyType.FREQUENCY ? FREQUENCY_FILE_NAME : BINAURAL_FILE_NAME;
        return YamlToJava.extract(fileName, FrequencyManager.class);
    }

    public enum FrequencyType
    {
        FREQUENCY, BINAURAL
    }
}
