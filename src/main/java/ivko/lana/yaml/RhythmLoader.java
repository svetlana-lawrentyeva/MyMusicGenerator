package ivko.lana.yaml;


import ivko.lana.musicentities.ChannelType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lana Ivko
 */
public abstract class RhythmLoader
{
    private static List<RhythmDetails> loadMelodyRhythmDetails(String rhythmSize)
    {
        return new MelodyRhythmLoader().loadRhythmDetails(rhythmSize);
    }

    private static List<RhythmDetails> loadChordRhythmDetails(String rhythmSize)
    {
        return new ChordRhythmLoader().loadRhythmDetails(rhythmSize);
    }

    public static List<RhythmDetails> loadRhythmDetails(String rhythmSize, ChannelType channelType)
    {
        switch (channelType)
        {
            case MELODY:
                return loadMelodyRhythmDetails(rhythmSize);
            case CHORD:
                return loadChordRhythmDetails(rhythmSize);
            default:
                throw new UnsupportedOperationException(String.format("rhythm detail is not supported for type: %s", channelType));
        }
    }

    protected List<RhythmDetails> loadRhythmDetails(String rhythmSize)
    {
        String rhythmDirectory = getRhythmDirectory();
        Setup setup = YamlToJava.extract(rhythmDirectory + File.separator + "setup.yaml", Setup.class);
        List<RhythmDetails> rhythmDetails = new ArrayList<>();
        if (rhythmSize == null)
        {
            rhythmDetails = loadAllRhythmSizeDetails(rhythmDirectory, setup.getBaseDurationMultiplier());
        }
        else
        {
            rhythmDetails = Collections.singletonList(loadRhythmDetails(rhythmSize, rhythmDirectory, setup.getBaseDurationMultiplier()));
        }

        return rhythmDetails;
    }

    private List<RhythmDetails> loadAllRhythmSizeDetails(String rhythmDirectory, int baseDurationMultiplier)
    {
        try
        {
            URL resourceUrl = RhythmLoader.class.getClassLoader().getResource(rhythmDirectory);
            if (resourceUrl != null)
            {
                // Преобразование URL в путь к директории
                Path resourcePath = Paths.get(resourceUrl.toURI());

                // Получение списка файлов в директории
                try (Stream<Path> paths = Files.list(resourcePath))
                {
                    return paths.filter(Files::isRegularFile)
                            .map(path -> path.getFileName().toString())
                            .map(fileName ->
                            {
                                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                                return loadRhythmDetails(fileName, rhythmDirectory, baseDurationMultiplier);
                            })
                            .collect(Collectors.toList());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static RhythmDetails loadRhythmDetails(String rhythmSize, String rhythmDirectory, int baseDurationMultiplier)
    {
        RhythmDetails rhythmDetails = YamlToJava.extract(rhythmDirectory + File.separator + rhythmSize + ".yaml", RhythmDetails.class);
        rhythmDetails.setBaseDurationMultiplier(baseDurationMultiplier);
        rhythmDetails.setName(rhythmSize);
        return rhythmDetails;
    }

    protected abstract String getRhythmDirectory();
}
