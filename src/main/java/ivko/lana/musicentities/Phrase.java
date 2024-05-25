package ivko.lana.musicentities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Phrase implements IPlayable
{
    private List<Rhythm> rhythms_;
    private int channel_;

    public Phrase(List<Rhythm> rhythms, int channel)
    {
        rhythms_ = rhythms;
        channel_ = channel;
    }

    @Override
    public int getChannelNumber()
    {
        return channel_;
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return rhythms_.stream()
                .flatMap(rhythm -> rhythm.getAllNotes().stream())
                .collect(Collectors.toList());
    }

    public List<Rhythm> getRhythms()
    {
        return rhythms_;
    }

    @Override
    public List<ISound> getAllSounds()
    {
        List<ISound> sounds = new ArrayList<>();
        for (Rhythm rhythm : rhythms_)
        {
            sounds.addAll(rhythm.getAllSounds());
        }
        return sounds;
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return rhythms_.stream()
                .map(rhythm -> (IPlayable) rhythm)
                .collect(Collectors.toList());
    }
}
