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
        super(phrases, instrumentCode);
    }

    @Override
    protected int getChannelNumber()
    {
        return 9;
    }
}
