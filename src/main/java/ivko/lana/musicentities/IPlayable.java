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
        int channelNumber = getChannelNumber();
//        LOGGER.info(String.format("%s (%s) - number: %s - starts playing", this.getClass().getSimpleName(), System.identityHashCode(this), channelNumber));
        for (IPlayable sound : getPlayables())
        {
            if (sound.equals(RhythmSeparator.SEPARATOR))
            {
//                LOGGER.info(String.format("%s (%s) - number: %s - found separator", this.getClass().getSimpleName(), System.identityHashCode(this), channelNumber));
                metronom.countDown();
//                LOGGER.info(String.format("%s (%s) - number: %s - after countDown: %s", this.getClass().getSimpleName(), System.identityHashCode(this), channelNumber, metronom.getCount()));
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
//        LOGGER.info(String.format("\t\t\t%s (%s) stops playing", this.getClass().getSimpleName(), System.identityHashCode(this)));
    }

    List<IPlayable> getPlayables();

    List<Integer> getAllNotes();

    List<ISound> getAllSounds();

    int getChannelNumber();
}
