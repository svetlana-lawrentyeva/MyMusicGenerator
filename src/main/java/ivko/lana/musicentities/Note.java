package ivko.lana.musicentities;

import ivko.lana.entities.IScale;

import javax.sound.midi.MidiChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Lana Ivko
 */
public class Note implements ISound
{
    private boolean shouldDebug_ = false;
    private final int tone_;
    private final int duration_;
    private final int accent_;

    public Note(int tone, int duration, int accent)
    {
        tone_ = tone;
        duration_ = duration;
        accent_ = accent;
    }

    public void setShouldDebug(boolean shouldDebug)
    {
        shouldDebug_ = shouldDebug;
    }

    public int getTone()
    {
        return tone_;
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
        channel.noteOn(IScale.BASE_NOTE + tone_, accent_);
        Thread.sleep(duration_);   // Hold the note for the duration
        channel.noteOff(IScale.BASE_NOTE + tone_, accent_);
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
    public boolean isSilent()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "Note{" +
                "tone_=" + tone_ +
                ", duration_=" + duration_ +
                ", accent_=" + accent_ +
                '}';
    }
}
