package ivko.lana.musicentities;

import ivko.lana.util.MusicUtil;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Channel implements IPlayable
{
    private final List<Part> parts_;
    private final int instrumentCode_;
    private boolean isMelody_ = false;
    private int channelNumber_;
    protected MidiChannel channel_;
    private Synthesizer synthesizer_;

    public Channel(List<Part> parts, int instrumentCode, int channelNumber)
    {
        parts_ = parts;
        instrumentCode_ = instrumentCode;
        channelNumber_ = channelNumber;
        try
        {
            synthesizer_ = prepareSynthesizer();

            LOGGER.info(String.format("%s (%s) number %s will be used", this.getClass().getSimpleName(), System.identityHashCode(this), channelNumber_));
            channel_ = synthesizer_.getChannels()[channelNumber_];
        } catch (MidiUnavailableException e)
        {
            throw new RuntimeException(e);
        }
    }

    public MidiChannel getChannel()
    {
        return channel_;
    }

    public void setIsMelody(boolean isMelody)
    {
        isMelody_ = isMelody;
    }

    public boolean isMelody()
    {
        return isMelody_;
    }


    public int getInstrumentCode()
    {
        return instrumentCode_;
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return parts_.stream()
                .map(sound -> (IPlayable) sound)
                .collect(Collectors.toList());
    }

    public int getChannelNumber()
    {
        return channelNumber_;
    }

    @Override
    public List<Integer> getAllNotes()
    {
        return parts_.stream()
                .flatMap(part -> part.getAllNotes().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<ISound> getAllSounds()
    {
        List<ISound> sounds = new ArrayList<>();
        for (Part part : parts_)
        {
            sounds.addAll(part.getAllSounds());
        }
        return sounds;
    }

    public void play(Metronom metronom) throws InterruptedException, MidiUnavailableException
    {
        initInstrument();
        playParts(metronom);
        stop();
    }

    protected void playParts(Metronom metronom) throws InterruptedException
    {
        for (Part part : parts_)
        {
            part.play(channel_, metronom);
        }
    }

    protected void initInstrument()
    {
        if (instrumentCode_ >= 0 && channelNumber_ != MusicUtil.DRUMS_CHANNEL_NUMBER)
        {
            Instrument[] instruments = synthesizer_.getDefaultSoundbank().getInstruments();
            synthesizer_.loadInstrument(instruments[instrumentCode_]);
            channel_.programChange(instrumentCode_);
        }
    }

    public void stop()
    {
        channel_.allNotesOff();
    }

    protected Synthesizer prepareSynthesizer() throws MidiUnavailableException
    {
        Synthesizer synthesizer = getSynthesizer();
        MusicUtil.getInstance().prepareSynthesizerIfNeeded();
//        LOGGER.info(String.format("channel %s '%s' with Instrument '%s' is playing", this.getClass().getSimpleName(), this.hashCode(), instruments[instrumentCode_]));
        return synthesizer;
    }

    private static Synthesizer getSynthesizer()
    {
        return MusicUtil.getInstance().getSynthesizer();
    }

    @Override
    public void play(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }
}
