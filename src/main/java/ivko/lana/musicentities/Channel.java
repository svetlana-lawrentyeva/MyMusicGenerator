package ivko.lana.musicentities;

import ivko.lana.util.MusicUtil;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Channel implements IPlayable
{
    private final List<Part> parts_;
    private final int instrumentCode_;
    private boolean isMelody_ = false;

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

    @Override
    public List<Integer> getAllNotes()
    {
        return parts_.stream()
                .flatMap(part -> part.getAllNotes().stream())
                .collect(Collectors.toList());
    }

    public void play(CountDownLatch metronom) throws InterruptedException
    {
//        if (instrumentCode_ == 0)
//            return;
        Synthesizer synthesizer = prepareSynthesizer();

        int channelNumber = getChannelNumber();
        MidiChannel channel = synthesizer.getChannels()[channelNumber];
        if (instrumentCode_ >= 0)
        {
            channel.programChange(instrumentCode_);
        }

        for (Part part : parts_)
        {
            part.play(channel, metronom);
        }
        synthesizer.close();
    }

    protected Synthesizer prepareSynthesizer()
    {
        Synthesizer synthesizer = MusicUtil.getInstance().getSynthesizer();
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        synthesizer.loadInstrument(instruments[instrumentCode_]);
        LOGGER.info(String.format("channel %s '%s' with Instrument '%s' is playing", this.getClass().getSimpleName(), this.hashCode(), instruments[instrumentCode_]));
        return synthesizer;
    }

    protected int getChannelNumber()
    {
        return MusicUtil.getInstance().getFreeChannelNumber();
    }

    @Override
    public void play(MidiChannel channel, CountDownLatch metronom) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }
}
