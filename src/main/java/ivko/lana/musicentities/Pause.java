package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Lana Ivko
 */
public class Pause implements ISound
{
    private final int duration_;

    public Pause(int duration_)
    {
        this.duration_ = duration_;
    }

    public int getDuration()
    {
        return duration_;
    }

    public void play(MidiChannel channel, CountDownLatch metronom) throws InterruptedException
    {
        Thread.sleep(duration_);   // Hold the note for the duration
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return Collections.singletonList(-1);
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return Arrays.asList(this);
    }

    @Override
    public boolean isSilent()
    {
        return true;
    }

    @Override
    public int getTone()
    {
        return -1;
    }

    @Override
    public int getAccent()
    {
        return -1;
    }

    @Override
    public String toString()
    {
        return "Pause{" +
                "duration_=" + duration_ +
                '}';
    }
}
