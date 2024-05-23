package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Part implements IPlayable
{
    private List<Phrase> phrases_;

    public Part(List<Phrase> phrases)
    {
        phrases_ = phrases;
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return phrases_.stream()
                .map(sound -> (IPlayable) sound)
                .collect(Collectors.toList());
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
