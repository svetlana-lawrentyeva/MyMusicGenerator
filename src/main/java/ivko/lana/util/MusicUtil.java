package ivko.lana.util;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Lana Ivko
 */
public class MusicUtil
{
    public static final int DRUMS_CHANNEL_NUMBER = 9;
    public static final int HERTZ_CHANNEL_NUMBER = 15;
    //    private static final int[] CHORDS_INSTRUMENT_CODES = {0};
//    private static final int[] MELODY_INSTRUMENT_CODES = {0};


//    private static int[] ChordsInstrumentCodes = {28, 30, 31, 33, 34, 0, 24, 25, 26, 32, 33, 34, 48, 49, 52};
//    private static int[] MelodyInstrumentCodes = {24, 0, 46, 73, 40, 42, 60, 118, 52, 80};

    private final Synthesizer synthesizer_;

    private SortedSet<Integer> channelsNumber_ = new TreeSet<>();

    private volatile AtomicBoolean isInitialized_ = new AtomicBoolean(false);
    private int usedInstrumentCounter_ = 0;

    private MusicUtil()
    {
        try
        {
            synthesizer_ = MidiSystem.getSynthesizer();
        }
        catch (MidiUnavailableException e)
        {
            uninitialize();
            throw new RuntimeException(e);
        }
    }

    public void uninitialize()
    {
        if (isInitialized_.get())
        {
            synthesizer_.close();
        }
    }

    public int getFreeChannelNumber()
    {
        int suggestedNumber = -1;
        boolean found = true;
        while (found)
        {
            found = channelsNumber_.contains(++suggestedNumber);
        }
        channelsNumber_.add(suggestedNumber);
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
        return isInitialized_.get();
    }

    public void resetFreeCanalNumbers()
    {
        isInitialized_.set(false);
        channelsNumber_.clear();
    }

    public void prepareSynthesizerIfNeeded() throws MidiUnavailableException
    {
        if (!isInitialized_.get())
        {
            synthesizer_.open();
            channelsNumber_.add(DRUMS_CHANNEL_NUMBER);
            channelsNumber_.add(HERTZ_CHANNEL_NUMBER);
            isInitialized_.set(true);
        }
    }

//    public int getNextInstrumentCode(boolean isMelody)
//    {
//        Integer[] instrumentCodes;
//        instrumentCodes = isMelody ?  : ChordsInstrumentCodes;
//        boolean usePresetInstruments = usedInstrumentCounter_ < instrumentCodes.length;
//        if (isMelody || usePresetInstruments)
//        {
//            int index = (int) (Math.random() * instrumentCodes.length);
//            if (!isMelody)
//            {
//                usedInstrumentCounter_++;
//            }
//            return instrumentCodes[index];
//        }
//        return (int) (Math.random() * 100);
//    }

    private static class InstanceHolder
    {
        private static final MusicUtil INSTANCE = new MusicUtil();
    }
}
