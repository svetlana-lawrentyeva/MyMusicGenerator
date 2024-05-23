package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Music implements IPlayable
{
    private List<Channel> channels_;
    private Metronom metronom_;

    public Music(List<Channel> channels)
    {
        channels_ = channels;
    }

    public List<Channel> getChannels()
    {
        return channels_;
    }

    public void play() throws InterruptedException
    {
        try
        {
//            LOGGER.info(String.format("%s '%s' is playing", this.getClass().getSimpleName(), this.hashCode()));
            List<Thread> threads = new ArrayList<>();
            metronom_ = new Metronom(channels_.size());
            for (Channel channel : channels_)
            {
//                if (channel.getChannelNumber() != 9)
//                {
//                    continue;
//                }
                Thread thread = new Thread(() ->
                {
                    try
                    {
                        channel.play(metronom_);
                    } catch (InterruptedException | MidiUnavailableException e)
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
        } catch (Throwable t)
        {
            LOGGER.severe(t.getLocalizedMessage());
            throw t;
        }
    }

    public void stop() throws InterruptedException
    {
        metronom_.stop();
        metronom_.await();
        for (Channel channel : channels_)
        {
            channel.stop();
        }
    }

    @Override
    public void play(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return channels_.stream()
                .map(channel -> (IPlayable) channel)
                .collect(Collectors.toList());
    }

    public List<Integer> getAllNotes()
    {
        Channel melodyChannel = channels_.stream()
                .filter(channel -> channel.isMelody())
                .findFirst()
                .orElse(null);
        List<Integer> result;
        if (melodyChannel != null)
        {
            result = melodyChannel.getAllNotes();
        } else
        {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    @Override
    public List<ISound> getAllSounds()
    {
        List<ISound> sounds = new ArrayList<>();
        for (Channel channel : channels_)
        {
            sounds.addAll(channel.getAllSounds());
        }
        return sounds;
    }
}
