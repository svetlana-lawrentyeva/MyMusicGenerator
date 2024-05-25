package ivko.lana.musicentities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Part implements IPlayable
{
    private List<Phrase> phrases_;
    private int channel_;

    public Part(List<Phrase> phrases, int channel)
    {
        phrases_ = phrases;
        channel_ = channel;
    }

    @Override
    public int getChannelNumber()
    {
        return channel_;
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return phrases_.stream()
                .map(sound -> (IPlayable) sound)
                .collect(Collectors.toList());
    }

    public List<Phrase> getPhrases()
    {
        return phrases_;
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return phrases_.stream()
                .flatMap(phrase -> phrase.getAllNotes().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<ISound> getAllSounds()
    {
        List<ISound> sounds = new ArrayList<>();
        for (Phrase phrase : phrases_)
        {
            sounds.addAll(phrase.getAllSounds());
        }
        return sounds;
    }
}
