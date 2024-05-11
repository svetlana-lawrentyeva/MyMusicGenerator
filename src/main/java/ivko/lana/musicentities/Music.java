package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class Music implements IPlayable
{
    private List<Channel> channels_;

    public Music(List<Channel> channels)
    {
        channels_ = channels;
    }

    public void play() throws InterruptedException
    {
        List<Thread> threads = new ArrayList<>();
        for (Channel channel : channels_)
        {
            Thread thread = new Thread(() ->
            {
                try
                {
                    channel.play();
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
    public void play(MidiChannel channel) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return null;
    }
}
