package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.Part;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class ChordChannelGenerator extends AccompanimentChannelGenerator
{
    protected int channelNumber_;

    private ChordRhythmGenerator rhythmGenerator_;

    public ChordChannelGenerator(Initializer initializer, int channelNumber)
    {
        super(initializer);
        channelNumber_ = channelNumber;
    }

    @Override
    protected AccompanimentRhythmGenerator createRhythmGenerator(Initializer initializer)
    {
        return  new ChordRhythmGenerator(initializer, channelNumber_);
    }

    @Override
    protected int getChannelNumber()
    {
        return channelNumber_;
    }

    protected Channel generateImpl(List<Part> parts)
    {
        return new Channel(parts, getInstrumentCode(), channelNumber_);
    }

    private int getInstrumentCode()
    {
//        return 0;
        List<Integer> chordInstrumentCodes = initializer_.getChordInstrumentCodes();
        int chordInstrumentIndex = (int) (Math.random() * chordInstrumentCodes.size());
        return chordInstrumentCodes.get(chordInstrumentIndex);
    }
}
