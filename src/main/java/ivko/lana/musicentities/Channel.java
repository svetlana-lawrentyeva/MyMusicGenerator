package ivko.lana.musicentities;

import ivko.lana.util.MusicUtil;

import javax.sound.midi.*;
import java.util.List;
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

    @Override
    public List<IPlayable> getPlayables()
    {
        return parts_.stream()
                .map(sound -> (IPlayable) sound)
                .collect(Collectors.toList());
    }

    public void play() throws InterruptedException
    {
        Synthesizer synthesizer = MusicUtil.getInstance().getSynthesizer();
        MidiChannel channel = synthesizer.getChannels()[getChannelNumber()];
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        synthesizer.loadInstrument(instruments[instrumentCode_]);
        channel.programChange(instrumentCode_);
        for (Part part : parts_)
        {
            part.play(channel);
        }
        synthesizer.close();
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
