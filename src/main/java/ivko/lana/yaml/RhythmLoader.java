package ivko.lana.yaml;


import ivko.lana.musicentities.ChannelType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public abstract class RhythmLoader
{
    private static List<RhythmPattern> loadMelodyPatterns()
    {
        return new MelodyRhythmLoader().loadPatterns();
    }
    private static List<RhythmPattern> loadChordPatterns()
    {
        return new ChordRhythmLoader().loadPatterns();
    }

    public static List<RhythmPattern> loadAllPatterns(ChannelType channelType)
    {
        switch (channelType)
        {
            case MELODY:
                return loadMelodyPatterns();
            case CHORD:
                return loadChordPatterns();
            default:
                throw new UnsupportedOperationException(String.format("rhythm pattern is not supported for type: %s", channelType));
        }
    }

    protected List<RhythmPattern> loadPatterns()
    {
        List<RhythmPattern> results = new ArrayList<>();

        try
        {
            String rhythmDirectory = getRhythmDirectory();
            URL url = RhythmPattern.class.getClassLoader().getResource(rhythmDirectory);
            if (url.getProtocol().equals("file"))
            {
                java.nio.file.Path dir = java.nio.file.Paths.get(url.toURI());
                try (java.nio.file.DirectoryStream<java.nio.file.Path> stream = java.nio.file.Files.newDirectoryStream(dir))
                {
                    for (java.nio.file.Path entry : stream)
                    {
                        String fileName = entry.getFileName().toString();
                        RhythmPattern rhythmPattern = YamlToJava.extract(rhythmDirectory + File.separator + fileName, RhythmPattern.class);
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

    protected abstract String getRhythmDirectory();
}
