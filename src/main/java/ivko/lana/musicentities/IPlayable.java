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

    default
    void play(MidiChannel channel) throws InterruptedException
    {
        for (IPlayable sound : getPlayables())
        {
            sound.play(channel);
        }
    }

    List<IPlayable> getPlayables();
}
