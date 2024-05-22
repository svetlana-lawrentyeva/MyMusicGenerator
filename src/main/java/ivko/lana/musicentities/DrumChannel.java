package ivko.lana.musicentities;

import ivko.lana.util.MusicUtil;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class DrumChannel extends Channel
{
    public DrumChannel(List<Part> phrases, int instrumentCode)
    {
        super(phrases, -1);
    }

    @Override
    public int getChannelNumber()
    {
        return 9;
    }

    protected Synthesizer prepareSynthesizer()
    {
        Synthesizer synthesizer = MusicUtil.getInstance().getSynthesizer();
        LOGGER.info(String.format("channel %s '%s' is playing", this.getClass().getSimpleName(), this.hashCode()));
        return synthesizer;
    }
}
