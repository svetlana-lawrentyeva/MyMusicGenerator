package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Rhythm implements ISound
{
    private List<ISound> sounds_;
    private int channel_;

    public Rhythm(List<ISound> sounds, int channel)
    {
        sounds_ = sounds;
        channel_ = channel;
    }

    @Override
    public int getDuration()
    {
        return sounds_.stream()
                .mapToInt(ISound::getDuration) // Преобразуем Stream<Note> в Stream<Integer>
                .sum();
    }

    @Override
    public int getChannelNumber()
    {
        return channel_;
    }

    public List<ISound> getSounds()
    {
        return sounds_;
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return sounds_.stream()
                .flatMap(sound -> sound.getAllNotes().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<ISound> getAllSounds()
    {
        return sounds_.stream()
                .flatMap(sound -> sound.getAllSounds().stream())
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
    public void play(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
//        simplifyIfNeeded();
        ISound.super.play(channel, metronom);
    }

//    private void simplifyIfNeeded()
//    {
//        ISound firstNote = sounds_.get(0);
//        if (firstNote instanceof Note)
//        {
//            sounds_ = Collections.singletonList(new Note(firstNote.getTone(), getDuration(), firstNote.getAccent(),
//                    ((Note) firstNote).getChannel(), initializer_.getMelodyRhythmPattern().getBaseDurationMultiplier()));
//        }
//    }

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
