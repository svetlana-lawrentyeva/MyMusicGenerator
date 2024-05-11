package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.entities.MajorScale;
import ivko.lana.entities.MinorScale;
import ivko.lana.musicentities.ChannelType;
import ivko.lana.musicentities.MusicType;
import ivko.lana.yaml.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class Initializer
{
    Logger LOGGER = Logger.getLogger(Initializer.class.getName());
    private RhythmPattern melodyRhythmPattern_;
    private RhythmPattern chordRhythmPattern_;
    private NextNoteProbabilities nextNoteProbabilities_;
    private ChordInstrumentsByMelody chordInstrumentsByMelody_;
    private int melodyInstrumentCode_;
    private IScale scale_;

    private int partsCount_;
    private int channelCount_;
    private boolean isChordSequenced_;
    private MusicType musicType_;

    public Initializer()
    {
        scale_ = initializeScale();
        melodyRhythmPattern_ = initializeRhythmPattern(ChannelType.MELODY);
        chordRhythmPattern_ = initializeRhythmPattern(ChannelType.CHORD);
        nextNoteProbabilities_ = initializeNextNoteProbabilities();
        melodyInstrumentCode_ = initializeMelodyInstrumentCode();
        partsCount_ = initializePartsCount();
        channelCount_ = initializeChannelCount();
        isChordSequenced_ = initializeIsChordSequenced();
        musicType_ = initializeMusicType();
        LOGGER.info(String.format("The system is initialized:\n\tmusicType_: %s\n\tscale: %s\n\tpartsCount: %s\n\tchannelCount: %s\n\tisChordSequenced: %s",
                musicType_, scale_.getClass().getSimpleName(), partsCount_, channelCount_, isChordSequenced_));
    }

    private MusicType initializeMusicType()
    {
        return (int) (Math.random() * 2) == 1 ? MusicType.RELAX : MusicType.EPIC;
    }

    private Integer[] getMelodyInstrumentCodes()
    {
        chordInstrumentsByMelody_ = ChordInstrumentsByMelodyLoader.load();
        // Получаем список инструментов и преобразуем его в массив типа Integer
        return chordInstrumentsByMelody_.getMelodyInstruments().toArray(new Integer[0]);
    }

    private boolean initializeIsChordSequenced()
    {
//        return (int) (Math.random() * 2) == 1;
        return false;
    }

    private RhythmPattern initializeRhythmPattern(ChannelType channelType)
    {
        List<RhythmPattern> rhythmPatterns = RhythmLoader.loadAllPatterns(channelType);
        int length = rhythmPatterns.size();
        int rhythmIndex = (int) (Math.random() * length);
        RhythmPattern rhythmPattern = rhythmPatterns.get(rhythmIndex);
        return rhythmPattern;
    }

    private NextNoteProbabilities initializeNextNoteProbabilities()
    {
        return NextNoteProbabilitiesLoader.loadNoteProbabilities(scale_ instanceof MajorScale);
    }

    private IScale initializeScale()
    {
        int scaleChoice = (int) (Math.random() * 2);
        boolean isMajor = scaleChoice == 0;
        return isMajor ? new MajorScale() : new MinorScale();
    }

    private int initializeMelodyInstrumentCode()
    {
        Integer[] melodyInstrumentCodes = getMelodyInstrumentCodes();
        int instrumentIndex = (int) (Math.random() * melodyInstrumentCodes.length);
//        int instrumentIndex = 0;
        return melodyInstrumentCodes[instrumentIndex];
    }

    private int initializePartsCount()
    {
        int partsCount = (int) (Math.random() * 10) + 2;
//        int partsCount = 2;
        return partsCount;
    }

    public int getPhraseCount()
    {
        int phraseCount = (int) (Math.random() * 4) + 4;
//        int phraseCount = 10;
        return phraseCount;
    }

    public int getRhythmsCount()
    {
        int rhythmsCount = (int) (Math.random() * 4) + 4;
//        int phraseCount = 10;
        return rhythmsCount;
    }

    private int initializeChannelCount()
    {
    int channelCount = (int) (Math.random() * 7) + 3;
//        int channelCount = 3;
        return channelCount;
    }

    public NextNoteProbabilities getNextNoteProbabilities()
    {
        return nextNoteProbabilities_;
    }

    public RhythmPattern getChordRhythmPattern()
    {
        return chordRhythmPattern_;
    }

    public RhythmPattern getMelodyRhythmPattern()
    {
        return melodyRhythmPattern_;
    }

    public int getMelodyInstrumentCode()
    {
        return melodyInstrumentCode_;
    }

    public IScale getScale()
    {
        return scale_;
    }

    public int getPartsCount()
    {
        return partsCount_;
    }

    public int getChannelCount()
    {
        return channelCount_;
    }

    public boolean isChordSequeced()
    {
        return isChordSequenced_;
    }

    public List<Integer> getChordInstrumentCodes()
    {
        return chordInstrumentsByMelody_.getChordInstruments(melodyInstrumentCode_);
    }
}
