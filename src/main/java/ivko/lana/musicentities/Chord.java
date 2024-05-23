package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Chord implements ISound
{
    private boolean isSequenced_;

    private final List<Note> sounds_;

    public Chord(List<Note> notes, boolean isSequenced)
    {
        sounds_ = notes;
        isSequenced_ = isSequenced;
    }

    @Override
    public int getDuration()
    {
        return isSequenced_
                ? sounds_.stream()
                .mapToInt(ISound::getDuration)
                .sum()
                : sounds_.stream()
                .mapToInt(ISound::getDuration) // Преобразуем Stream<Note> в Stream<Integer>
                .max()
                .orElse(0);
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return sounds_.stream()
                .map(sound -> (IPlayable) sound)
                .collect(Collectors.toList());
    }

    public void play(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
        if (!isSequenced_)
        {
            playRealChord(channel, metronom);
        } else
        {
            ISound.super.play(channel, metronom);
        }
    }

    private void playRealChord(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
        List<Thread> threads = new ArrayList<>();
        for (ISound note : sounds_)
        {
            Thread thread = new Thread(() ->
            {
                try
                {
                    note.play(channel, metronom);
                } catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            });
            thread.start(); // Запускаем поток
            threads.add(thread); // Добавляем поток в список
        }
        // Ожидание завершения всех потоков
        for (Thread thread : threads)
        {
            thread.join(); // Ожидаем завершения потока
        }
    }

    @Override
    public boolean isSilent()
    {
        return false;
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return Collections.singletonList(getTone());
    }

    @Override
    public List<ISound> getAllSounds()
    {
        return new ArrayList<>(sounds_);
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
                .mapToInt(ISound::getDuration) // Преобразуем Stream<Note> в Stream<Integer>
                .min()
                .orElse(-1);
    }

    @Override
    public String toString()
    {
        return "Chord{" +
                "isSequenced_=" + isSequenced_ +
                ", sounds_=" + sounds_ +
                '}';
    }
}
