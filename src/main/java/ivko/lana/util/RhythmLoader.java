package ivko.lana.util;


import ivko.lana.entities.RhythmPattern;

/**
 * @author Lana Ivko
 */
public class RhythmLoader
{
    public static final String RHYTHM_4_4 = "rhythms/6-8.yaml";
    public static final String RHYTHM_3_4 = "rhythms/3-4.yaml";
    public static String[] ALL_RHYTHMS = {RHYTHM_4_4};

    public static RhythmPattern loadRhythm(String rhythmPath)
    {
        RhythmPattern extractedRhythm = YamlToJava.extract(rhythmPath, RhythmPattern.class);
        return extractedRhythm;
    }
}
