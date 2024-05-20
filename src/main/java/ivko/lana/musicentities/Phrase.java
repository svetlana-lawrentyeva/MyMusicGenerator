package ivko.lana.musicentities;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Phrase implements IPlayable
{
    private List<Rhythm> rhythms_;

    public Phrase(List<Rhythm> rhythms)
    {
        rhythms_ = rhythms;
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return rhythms_.stream()
                .flatMap(rhythm -> rhythm.getAllNotes().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return rhythms_.stream()
                .map(rhythm -> (IPlayable) rhythm)
                .collect(Collectors.toList());
    }
}
