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
    private final int baseDurationMultiplier_;

    public Note(int tone, int duration, int accent, int channel, int baseDurationMultiplier)
    {
        tone_ = tone;
        duration_ = duration;
        accent_ = accent;
        channel_ = channel;
        baseDurationMultiplier_ = baseDurationMultiplier;
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
//        return (channel_ != MusicUtil.DRUMS_CHANNEL_NUMBER && channel_ != MusicUtil.HERTZ_CHANNEL_NUMBER ? IScale.BASE_NOTE : 0) + tone_;
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
            LOGGER.info(String.format("%s '%s' is playing", System.identityHashCode(this), this));
        }
        channel.noteOn(getTone() + IScale.BASE_NOTE, accent_);
        Thread.sleep((long) duration_ * baseDurationMultiplier_);   // Hold the note for the duration
        if (shouldDebug_)
        {
            LOGGER.info(String.format("%s '%s' stop playing", System.identityHashCode(this), this));
        }
        channel.noteOff(getTone() + IScale.BASE_NOTE, accent_);
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
    public int getChannelNumber()
    {
        return channel_;
    }

    @Override
    public String toString()
    {
        return "Note{" +
                ", tone_=" + tone_ +
                ", duration_=" + duration_ +
                ", accent_=" + accent_ +
                ", channel_=" + channel_ +
                '}';
    }
}
