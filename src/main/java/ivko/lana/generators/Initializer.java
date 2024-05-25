package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.entities.MajorScale;
import ivko.lana.musicentities.ChannelType;
import ivko.lana.musicentities.MusicType;
import ivko.lana.util.MusicUtil;
import ivko.lana.yaml.*;

import javax.sound.midi.Synthesizer;
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
    private int soloInstrumentCode_;
    private DrumCombinations drumCombinations_;
    private IScale scale_;

    private boolean useDrums_ = true;

    private int hertz_ = HertzChannelGenerator.PINEAL_GLAND;

    private int rhythmsCount_;
    private int partsCount_;
    private int channelCount_;
    private boolean isChordSequenced_;
    private MusicType musicType_;

    private int testChannelCount_ = 3;
    private boolean isTest_ = true;
    private int minutes_ = 60;

    public Initializer()
    {
        scale_ = initializeScale();
        melodyRhythmPattern_ = initializeRhythmPattern(ChannelType.MELODY);
        chordRhythmPattern_ = initializeRhythmPattern(ChannelType.CHORD);
        nextNoteProbabilities_ = initializeNextNoteProbabilities();
        soloInstrumentCode_ = initializeMelodyInstrumentCode();
        chordInstrumentsByMelody_ = ChordInstrumentsByMelodyLoader.load();
        drumCombinations_ = initializeDrumCombinations();
        partsCount_ = initializePartsCount();
        channelCount_ = initializeChannelCount();
        isChordSequenced_ = initializeIsChordSequenced();
        musicType_ = initializeMusicType();
        rhythmsCount_ = initializeRhythmsCount();
        LOGGER.info(String.format("The system is initialized:\n\tmusicType_: %s\n\tscale: %s\n\tpartsCount: %s\n\tchannelCount: %s\n\tisChordSequenced: %s",
                musicType_, scale_.toString(), partsCount_, channelCount_, isChordSequenced_));
    }

    private int initializeRhythmsCount()
    {
        RhythmPattern rhythmPattern = getMelodyRhythmPattern();
        List<Integer> accents = rhythmPattern.getAccents();
        int measureLength = rhythmPattern.getBaseDuration() * accents.size();
        int totalLength = getMinutes() * 60 * 1000;
        int measureCount = totalLength / measureLength;
        int partsCount = getPartsCount();
        int measurePerPart = measureCount / partsCount;
        int phraseCount = getPhraseCount() * 2;
        return measurePerPart / phraseCount;
    }

    public int getMinutes()
    {
        return minutes_;
    }

    private DrumCombinations initializeDrumCombinations()
    {
        return DrumCombinationsLoader.load();
    }

    public List<Integer> getDrumCombinations()
    {
        List<List<Integer>> combinations = drumCombinations_.getCombinations();
        return combinations.get((int) Math.random() * combinations.size());
    }

    private MusicType initializeMusicType()
    {
//        return (int) (Math.random() * 2) == 1 ? MusicType.RELAX : MusicType.EPIC;
        return  MusicType.RELAX;
    }

    public boolean useDrums()
    {
        return useDrums_;
    }

    public int getHertz()
    {
        return hertz_;
    }

    public MusicType getMusicType()
    {
        return musicType_;
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
        return true;
    }

    private RhythmPattern initializeRhythmPattern(ChannelType channelType)
    {
        return RhythmLoader.loadAllPatterns(scale_.getRhythmSize(), channelType);
    }

    private NextNoteProbabilities initializeNextNoteProbabilities()
    {
        return NextNoteProbabilitiesLoader.loadNoteProbabilities(scale_ instanceof MajorScale);
    }

    private IScale initializeScale()
    {
        MusicScalesConfig musicScalesConfig = MusicScaleConfigLoader.load();
        List<MusicScalesConfig.ScaleConfig> scales = musicScalesConfig.getScales();
        int index = (int) (Math.random() * scales.size());
        MusicScalesConfig.ScaleConfig scaleConfig = scales.get(index);
        LOGGER.info(String.format("Playing: %s", scaleConfig.getName()));
        return scaleConfig;
    }

    private int initializeMelodyInstrumentCode()
    {
//        Integer[] melodyInstrumentCodes = getMelodyInstrumentCodes();
//        int instrumentIndex = (int) (Math.random() * melodyInstrumentCodes.length);
//        int instrumentIndex = 0;
        return scale_.getSoloInstrument();
    }

    private int initializePartsCount()
    {
        int partsCount = isTest_ ? 2 : (int) (Math.random() * 10) + 2;
        return partsCount;
    }

    public int getPhraseCount()
    {
        int phraseCount = isTest_ ? 2 : (int) (Math.random() * 4) + 4;
        return phraseCount;
    }

    public int getRhythmsCount()
    {
//        int rhythmsCount = isTest_ ? 2 : (int) (Math.random() * 4) + 4;
//        return rhythmsCount;
        return rhythmsCount_;
    }

    private int initializeChannelCount()
    {
    int channelCount = isTest_ ? testChannelCount_ : (int) (Math.random() * 7);
        Synthesizer synthesizer = MusicUtil.getInstance().getSynthesizer();
        int possibleChannelCount = synthesizer.getChannels().length - 1;
        return Math.min(channelCount, possibleChannelCount);
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
        return soloInstrumentCode_;
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
        return chordInstrumentsByMelody_.getChordInstruments(soloInstrumentCode_);
    }
}
