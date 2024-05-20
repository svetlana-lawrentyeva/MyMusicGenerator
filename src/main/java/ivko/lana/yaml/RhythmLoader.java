package ivko.lana.yaml;


import ivko.lana.musicentities.ChannelType;

import java.io.File;

/**
 * @author Lana Ivko
 */
public abstract class RhythmLoader
{
    private static RhythmPattern loadMelodyPatterns(String rhythmSize)
    {
        return new MelodyRhythmLoader().loadPattern(rhythmSize);
    }
    private static RhythmPattern loadChordPatterns(String rhythmSize)
    {
        return new ChordRhythmLoader().loadPattern(rhythmSize);
    }

    public static RhythmPattern loadAllPatterns(String rhythmSize, ChannelType channelType)
    {
        switch (channelType)
        {
            case MELODY:
                return loadMelodyPatterns(rhythmSize);
            case CHORD:
                return loadChordPatterns(rhythmSize);
            default:
                throw new UnsupportedOperationException(String.format("rhythm pattern is not supported for type: %s", channelType));
        }
    }

    protected RhythmPattern loadPattern(String rhythmSize)
    {
        String rhythmDirectory = getRhythmDirectory();
        RhythmPattern rhythmPattern = YamlToJava.extract(rhythmDirectory + File.separator + rhythmSize + ".yaml", RhythmPattern.class);
        Setup setup = YamlToJava.extract(rhythmDirectory + File.separator +"setup.yaml", Setup.class);
        rhythmPattern.setBaseDurationMultiplier(setup.getBaseDurationMultiplier());
        return rhythmPattern;
    }

    protected abstract String getRhythmDirectory();
}
