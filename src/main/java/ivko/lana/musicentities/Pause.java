package ivko.lana.musicentities;

import javax.sound.midi.MidiChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class Pause implements ISound
{
    private final int duration_;
    private final int baseDurationMultiplier_;
    private final int channel_;

    public Pause(int duration, int baseDurationMultiplier, int channel)
    {
        duration_ = duration;
        baseDurationMultiplier_ = baseDurationMultiplier;
        channel_ = channel;
    }

    @Override
    public int getChannelNumber()
    {
        return channel_;
    }

    public int getDuration()
    {
        return duration_;
    }

    public void play(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
        Thread.sleep(duration_ * baseDurationMultiplier_);   // Hold the note for the duration
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return Collections.singletonList(-1);
    }

    @Override
    public List<ISound> getAllSounds()
    {
        return Collections.singletonList(this);
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
        return 0;
    }

    @Override
    public int getAccent()
    {
        return 0;
    }

    @Override
    public String toString()
    {
        return "Pause{" +
                "duration_=" + duration_ +
                '}';
    }
}
