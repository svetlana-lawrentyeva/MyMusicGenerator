package ivko.lana.musicentities;

import ivko.lana.entities.IScale;

import javax.sound.midi.MidiChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class Note implements ISound
{
    private boolean shouldDebug_ = false;
    private final int tone_;
    private final int duration_;
    private final int accent_;
    private final int channel_;

    public Note(int tone, int duration, int accent, int channel)
    {
        tone_ = tone;
        duration_ = duration;
        accent_ = accent;
        channel_ = channel;
    }

    public int getChannel()
    {
        return channel_;
    }

    public void setShouldDebug(boolean shouldDebug)
    {
        shouldDebug_ = shouldDebug;
    }

    public int getTone()
    {
        return (channel_ != 9 ? IScale.BASE_NOTE : 0) + tone_;
    }

    public int getDuration()
    {
        return duration_;
    }

    public int getAccent()
    {
        return accent_;
    }

    public void play(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
        if (shouldDebug_)
        {
            LOGGER.info(String.format("%s '%s' is playing", this.getClass().getSimpleName(), this));
        }
        channel.noteOn(getTone(), accent_);
        Thread.sleep(duration_);   // Hold the note for the duration
        channel.noteOff(getTone(), accent_);
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return Arrays.asList(this);
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return Collections.singletonList(tone_);
    }

    @Override
    public List<ISound> getAllSounds()
    {
        return Collections.singletonList(this);
    }

    @Override
    public boolean isSilent()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "Note{" +
                "shouldDebug_=" + shouldDebug_ +
                ", tone_=" + tone_ +
                ", duration_=" + duration_ +
                ", accent_=" + accent_ +
                ", channel_=" + channel_ +
                '}';
    }
}
