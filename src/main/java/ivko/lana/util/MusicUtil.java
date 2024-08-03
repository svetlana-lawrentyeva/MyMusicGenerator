package ivko.lana.util;

import ivko.lana.yaml.ChordLibraryLoader;
import ivko.lana.yaml.DurationProbability;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Lana Ivko
 */
public class MusicUtil
{
    private static final String SAVE_DIRECTORY = "/home/therioyo/IdeaProjects/MyMusicGenerator/genearated/";
    protected static final Random Random = new Random();
    public static final int MELODY_CHANNEL_NUMBER = 0;
    public static final int DRUMS_CHANNEL_NUMBER = 9;
    public static final int HERTZ_CHANNEL_NUMBER = 15;

    private final Synthesizer synthesizer_;
    private ChordLibrary chordLibrary_;

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

    public static String generateNewName()
    {
        Date now = new Date();

        // Определение формата
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HHmmss");

        // Форматирование даты
        return SAVE_DIRECTORY + dateFormat.format(now);
    }

    public ChordLibrary getChordLibrary()
    {
        if (chordLibrary_ == null)
        {
            chordLibrary_ = ChordLibraryLoader.load();
        }
        return chordLibrary_;
    }

    public int generateConsiderProbability(Map<Integer, Double> valueToProbability)
    {
        double randomValue = Random.nextDouble(); // Случайное число от 0.0 до 1.0
        double cumulativeProbability = 0.0;

        List<Map.Entry<Integer, Double>> entries = new ArrayList<>(valueToProbability.entrySet());
        entries.sort(Comparator.comparingDouble(Map.Entry::getValue));
        int durationResult = 0;
        for (int i = 0; i < entries.size(); i++)
        {
            Map.Entry<Integer, Double> entry = entries.get(i);
            int value = entry.getKey();
            cumulativeProbability += entry.getValue();
            if (randomValue < cumulativeProbability)
            {
                durationResult = value;
                break;
            }
        }
        if (durationResult == 0)
        {
            durationResult = entries.get(entries.size() - 1).getKey(); // На случай числовых ошибок, возвращаем последний элемент
        }
        return durationResult;
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
        int suggestedNumber = 0;
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
