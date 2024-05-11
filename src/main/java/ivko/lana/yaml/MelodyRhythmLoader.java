package ivko.lana.yaml;

/**
 * @author Lana Ivko
 */
public class MelodyRhythmLoader extends RhythmLoader
{
    public static final String RHYTHM_DIRECTORY = "melody_rhythms";
//    public static final String RHYTHM_DIRECTORY = "rhythmsTest";


    @Override
    protected String getRhythmDirectory()
    {
        return RHYTHM_DIRECTORY;
    }
}
