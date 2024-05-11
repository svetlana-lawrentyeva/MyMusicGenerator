package ivko.lana.yaml;

/**
 * @author Lana Ivko
 */
public class ChordRhythmLoader extends RhythmLoader
{
    public static final String RHYTHM_DIRECTORY = "chord_rhythms";
    @Override
    protected String getRhythmDirectory()
    {
        return RHYTHM_DIRECTORY;
    }
}
