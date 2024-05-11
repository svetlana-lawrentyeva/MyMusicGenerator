package ivko.lana.musicentities;

import ivko.lana.util.MusicUtil;

import javax.sound.midi.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Channel implements IPlayable
{
    private final List<Part> parts_;
    private final int instrumentCode_;

    public Channel(List<Part> phrases, int instrumentCode)
    {
        parts_ = phrases;
        instrumentCode_ = instrumentCode;
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

    public void play() throws InterruptedException
    {
//        if (instrumentCode_ == 0)
//            return;
        Synthesizer synthesizer = prepareSynthesizer();

        int channelNumber = getChannelNumber();
        MidiChannel channel = synthesizer.getChannels()[channelNumber];
        if (instrumentCode_ >=0)
        {
            channel.programChange(instrumentCode_);
        }

        for (Part part : parts_)
        {
            part.play(channel);
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
    public void play(MidiChannel channel) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }
}
