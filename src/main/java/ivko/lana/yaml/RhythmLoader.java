package ivko.lana.yaml;


import ivko.lana.yaml.RhythmPattern;
import ivko.lana.yaml.YamlToJava;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class RhythmLoader
{
    public static final String RHYTHM_DIRECTORY = "rhythms";
//    public static final String RHYTHM_DIRECTORY = "rhythmsTest";

    public static List<RhythmPattern> loadAllPatterns()
    {
        List<RhythmPattern> results = new ArrayList<>();

        try
        {
            URL url = RhythmPattern.class.getClassLoader().getResource(RHYTHM_DIRECTORY);
            if (url.getProtocol().equals("file"))
            {
                java.nio.file.Path dir = java.nio.file.Paths.get(url.toURI());
                try (java.nio.file.DirectoryStream<java.nio.file.Path> stream = java.nio.file.Files.newDirectoryStream(dir))
                {
                    for (java.nio.file.Path entry : stream)
                    {
                        String fileName = entry.getFileName().toString();
                        RhythmPattern rhythmPattern = YamlToJava.extract(RHYTHM_DIRECTORY + File.separator + fileName, RhythmPattern.class);
                        results.add(rhythmPattern);
                    }
                }
            }
        } catch (URISyntaxException | IOException e)
        {
            throw new RuntimeException(e);
        }
        return results;
    }
}
