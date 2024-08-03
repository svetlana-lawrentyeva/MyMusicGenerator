package ivko.lana.yaml;

import java.io.File;

/**
 * @author Lana Ivko
 */
public class NextNoteProbabilitiesLoader
{
    public static final String NEXT_NOTE_PROBABILITY_DIRECTORY = "predictions";
    public static final String MAJOR_FILE_NAME = "predictions.yaml";
    public static final String MINOR_FILE_NAME = "minorPredictions.yaml";

    public static NextNoteProbabilities loadNoteProbabilities()
    {
        String fileName = MAJOR_FILE_NAME;
        return YamlToJava.extract(NEXT_NOTE_PROBABILITY_DIRECTORY + File.separator + fileName, NextNoteProbabilities.class);
    }
}
