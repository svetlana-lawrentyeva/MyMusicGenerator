package ivko.lana.musicentities;

import ivko.lana.util.MusicUtil;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
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
    private MidiChannel channel_;

    public Channel(List<Part> phrases, int instrumentCode)
    {
        parts_ = phrases;
        instrumentCode_ = instrumentCode;
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

    public void play(Metronom metronom) throws InterruptedException, MidiUnavailableException
    {
//        if (instrumentCode_ == 0)
//            return;
        Synthesizer synthesizer = prepareSynthesizer();

        channelNumber_ = MusicUtil.getInstance().getFreeChannelNumber();
        channel_ = synthesizer.getChannels()[channelNumber_];
        if (instrumentCode_ >= 0)
        {
            channel_.programChange(instrumentCode_);
        }

        for (Part part : parts_)
        {
            part.play(channel_, metronom);
        }
        synthesizer.close();
    }

    public void stop()
    {
        channel_.allNotesOff();
        Synthesizer synthesizer = getSynthesizer();
        synthesizer.close();
        MusicUtil.getInstance().resetFreeCanalNumbers();
    }

    protected Synthesizer prepareSynthesizer() throws MidiUnavailableException
    {
        Synthesizer synthesizer = getSynthesizer();
        MusicUtil.getInstance().prepareSynthesizer();
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        synthesizer.loadInstrument(instruments[instrumentCode_]);
        LOGGER.info(String.format("channel %s '%s' with Instrument '%s' is playing", this.getClass().getSimpleName(), this.hashCode(), instruments[instrumentCode_]));
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
