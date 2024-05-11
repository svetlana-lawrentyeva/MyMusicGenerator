package ivko.lana.musicentities;

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
    public List<IPlayable> getPlayables()
    {
        return rhythms_.stream()
                .map(sound -> (IPlayable) sound)
                .collect(Collectors.toList());
    }
}
