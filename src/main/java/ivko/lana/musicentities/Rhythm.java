package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Rhythm implements ISound
{
    private List<ISound> sounds_;

    public Rhythm(List<ISound> sounds)
    {
        sounds_ = sounds;
    }
    @Override
    public int getDuration()
    {
        return sounds_.stream()
                .mapToInt(ISound::getDuration) // Преобразуем Stream<Note> в Stream<Integer>
                .sum();
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return sounds_.stream()
                .flatMap(sound -> sound.getAllNotes().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return sounds_.stream()
                .map(sound -> (IPlayable) sound)
                .collect(Collectors.toList());
    }

    @Override
    public void play(MidiChannel channel, CountDownLatch metronom) throws InterruptedException
    {
//        simplifyIfNeeded();
        ISound.super.play(channel, metronom);
    }

    private void simplifyIfNeeded()
    {
        ISound firstNote = sounds_.get(0);
        if (firstNote instanceof Note)
        {
            sounds_ = Collections.singletonList(new Note(firstNote.getTone(), getDuration(), firstNote.getAccent()));
        }
    }

    @Override
    public boolean isSilent()
    {
        return sounds_.stream()
                .allMatch(ISound::isSilent);
    }

    @Override
    public int getTone()
    {
        return sounds_.stream()
                .mapToInt(ISound::getTone) // Преобразуем Stream<Note> в Stream<Integer>
                .min()
                .orElse(-1);
    }

    @Override
    public int getAccent()
    {
        return sounds_.stream()
                .mapToInt(ISound::getAccent) // Преобразуем Stream<Note> в Stream<Integer>
                .min()
                .orElse(-1);
    }
}
