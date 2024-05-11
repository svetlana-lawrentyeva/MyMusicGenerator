package ivko.lana.yaml;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Lana Ivko
 */
public class NextNoteProbabilitiesLoader
{
    public static final String NEXT_NOTE_PROBABILITY_DIRECTORY = "predictions";
    public static final String MAJOR_FILE_NAME = "majorPredictions.yaml";
    public static final String MINOR_FILE_NAME = "minorPredictions.yaml";
    public static NextNoteProbabilities loadNoteProbabilities(boolean isMajor)
    {
        String fileName = isMajor ? MAJOR_FILE_NAME : MINOR_FILE_NAME;
        return YamlToJava.extract(NEXT_NOTE_PROBABILITY_DIRECTORY + File.separator + fileName, NextNoteProbabilities.class);
    }
}
