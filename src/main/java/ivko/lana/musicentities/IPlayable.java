package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public interface IPlayable
{
    Logger LOGGER = Logger.getLogger(IPlayable.class.getName());

    default
    void play(MidiChannel channel, CountDownLatch metronom) throws InterruptedException
    {
        for (IPlayable sound : getPlayables())
        {
            if (sound.equals(RhythmSeparator.SEPARATOR))
            {
                metronom.countDown();
                metronom.await();
            }
            else
            {
                sound.play(channel, metronom);
            }
        }
    }

    List<IPlayable> getPlayables();

    List<Integer> getAllNotes();
}
