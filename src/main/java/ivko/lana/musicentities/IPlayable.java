package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public interface IPlayable
{
    Logger LOGGER = Logger.getLogger(IPlayable.class.getName());

    default void play(MidiChannel channel, Metronom metronom) throws InterruptedException
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
                if (!metronom.shouldStop())
                {
                    sound.play(channel, metronom);
                }
                else
                {
                    channel.allNotesOff();
                }
            }
        }
    }

    List<IPlayable> getPlayables();

    List<Integer> getAllNotes();
}
