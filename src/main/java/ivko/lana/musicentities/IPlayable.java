package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.List;

/**
 * @author Lana Ivko
 */
public interface IPlayable
{
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
