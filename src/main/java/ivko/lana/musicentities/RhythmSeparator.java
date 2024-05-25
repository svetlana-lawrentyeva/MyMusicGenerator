package ivko.lana.musicentities;

import java.util.Collections;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class RhythmSeparator extends Rhythm
{
    public static final RhythmSeparator SEPARATOR = new RhythmSeparator();
    private RhythmSeparator(List<ISound> sounds)
    {
        super(sounds, -1);
    }

    private RhythmSeparator(){
        super(null, -1);
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ISound> getAllSounds()
    {
        return Collections.EMPTY_LIST;
    }
}
