package ivko.lana.util;

import ivko.lana.yaml.ChordInstrumentsByMelody;
import ivko.lana.yaml.ChordInstrumentsByMelodyLoader;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.*;

/**
 * @author Lana Ivko
 */
public class MusicUtil
{
//    private static final int[] CHORDS_INSTRUMENT_CODES = {0};
//    private static final int[] MELODY_INSTRUMENT_CODES = {0};


//    private static int[] ChordsInstrumentCodes = {28, 30, 31, 33, 34, 0, 24, 25, 26, 32, 33, 34, 48, 49, 52};
//    private static int[] MelodyInstrumentCodes = {24, 0, 46, 73, 40, 42, 60, 118, 52, 80};

    private final Synthesizer synthesizer_;

    private SortedSet<Integer> channelsNumber_ = new TreeSet<>();

    private boolean isInitialized_ = false;
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
        if (isInitialized_)
        {
            synthesizer_.close();
        }
    }

    public int getFreeChannelNumber()
    {
        int suggestedNumber = 0;
        boolean found = true;
        while (found)
        {
            found = channelsNumber_.contains(suggestedNumber);
            if (found)
            {
                suggestedNumber++;
            }
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
        return isInitialized_;
    }

    public void resetFreeCanalNumbers()
    {
        channelsNumber_.clear();
    }

    public void prepareSynthesizer() throws MidiUnavailableException
    {
        synthesizer_.open();
        isInitialized_ = true;
        channelsNumber_.add(9);
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
