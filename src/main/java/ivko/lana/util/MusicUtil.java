package ivko.lana.util;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Lana Ivko
 */
public class MusicUtil
{
    private static final int[] CHORDS_INSTRUMENT_CODES = {28, 30, 31, 33, 34};
    private static final int[] MELODY_INSTRUMENT_CODES = {40, 65, 66, 64, 67};

    private final Synthesizer synthesizer_;

    private SortedSet<Integer> channelsNumber_ = new TreeSet<>();

    private boolean isInitialized_ = false;
    private int usedInstrumentCounter_ = 0;

    private MusicUtil()
    {
        try
        {
            synthesizer_ = MidiSystem.getSynthesizer();
            synthesizer_.open();
            isInitialized_ = true;
        }
        catch (MidiUnavailableException e)
        {
            uninitialize();
            throw new RuntimeException(e);
        }
    }

    public void uninitialize()
    {
        if (isInitialized_)
        {
            synthesizer_.close();
        }
    }

    public int getFreeChannelNumber()
    {
        int suggestedNumber = 0;
        for (int number : channelsNumber_)
        {
            if (number != suggestedNumber && number != 9)
            {
                break;
            }
            else
            {
                suggestedNumber = number + 1;
            }
        }
        return suggestedNumber;
    }

    public static MusicUtil getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    public Synthesizer getSynthesizer()
    {
        return synthesizer_;
    }

    public boolean isInitialized()
    {
        return isInitialized_;
    }

    public int getNextInstrumentCode(boolean isMelody)
    {
        int[] instrumentCodes = isMelody ? MELODY_INSTRUMENT_CODES : CHORDS_INSTRUMENT_CODES;
        boolean usePresetInstruments = usedInstrumentCounter_ < instrumentCodes.length;
        if (usePresetInstruments)
        {
            int index = (int) (Math.random() * instrumentCodes.length);
            usedInstrumentCounter_++;
            return instrumentCodes[index];
        }
        return (int) (Math.random() * 100);
    }

    private static class InstanceHolder
    {
        private static final MusicUtil INSTANCE = new MusicUtil();
    }
}
