package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.ChannelType;
import ivko.lana.musicentities.MusicType;
import ivko.lana.util.MusicUtil;
import ivko.lana.yaml.*;

import javax.sound.midi.Synthesizer;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Initializer
{
    private static final boolean IS_TEST = false;


    Logger LOGGER = Logger.getLogger(Initializer.class.getName());
    private RhythmDetails melodyPrimaryRhythmDetails_;
    private RhythmDetails chordPrimaryRhythmDetails_;
    private List<RhythmDetails> melodyRhythmDetails_;
    private List<RhythmDetails> chordRhythmDetails_;
    private NextNoteProbabilities nextNoteProbabilities_;
    private ChordInstrumentsByMelody chordInstrumentsByMelody_;
    private int soloInstrumentCode_;
    private DrumCombinations drumCombinations_;
    private List<IScale> scales_;
    private IScale scale_;

    private boolean useDrums_ = true;

    //    private int hertz_ = HertzChannelGenerator.PINEAL_GLAND;
    private int hertz_ = HertzChannelGenerator.DNA;

    private int rhythmsCount_;
    private int partsCount_;
    private int channelCount_ = 0;
    private boolean isChordSequenced_;
    private MusicType musicType_;

    private int testChannelCount_ = 0;
    private int minutes_ = 5;

    public Initializer()
    {
        scales_ = initializeScales();
        nextNoteProbabilities_ = initializeNextNoteProbabilities();
        chordInstrumentsByMelody_ = ChordInstrumentsByMelodyLoader.load();
        drumCombinations_ = initializeDrumCombinations();
        partsCount_ = initializePartsCount();
        channelCount_ = initializeChannelCount();
        isChordSequenced_ = initializeIsChordSequenced();
        musicType_ = initializeMusicType();
        rhythmsCount_ = initializeRhythmsCount();

        preInitialize();
        LOGGER.info(String.format("The system is initialized:\n\tmusicType_: %s\n\tscale: %s\n\tpartsCount: %s\n\tchannelCount: %s\n\tisChordSequenced: %s",
                musicType_, scale_.toString(), partsCount_, channelCount_, isChordSequenced_));
    }

    private void preInitialize()
    {
        int index = (int) (Math.random() * scales_.size());
        setScale(scales_.get(index));
        melodyPrimaryRhythmDetails_ = melodyRhythmDetails_.get(0);
        chordPrimaryRhythmDetails_ = chordRhythmDetails_.get(0);
    }

    private int initializeRhythmsCount()
    {
        return (int) (Math.random() * 3 + 2);
//        RhythmPattern rhythmPattern = getMelodyRhythmPattern();
//        List<Integer> accents = rhythmPattern.getAccents();
//        int measureLength = rhythmPattern.getBaseDuration() * accents.size();
//        int totalLength = getMinutes() * 60 * 1000;
//        int measureCount = totalLength / measureLength;
//        int partsCount = getPartsCount();
//        int measurePerPart = measureCount / partsCount;
//        int phraseCount = getPhraseCount() * 2;
//        return measurePerPart / phraseCount;
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
        return MusicType.RELAX;
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
        return false;
    }

    public void setMelodyPrimaryRhythmDetails(RhythmDetails rhythmDetails)
    {
        melodyPrimaryRhythmDetails_ = rhythmDetails;
        LOGGER.info(String.format("Set melody PRIMARY rhythm details: %s", melodyRhythmDetails_));
    }

    public void setChordPrimaryRhythmDetails(RhythmDetails rhythmDetails)
    {
        chordPrimaryRhythmDetails_ = rhythmDetails;
        LOGGER.info(String.format("Set chord PRIMARY rhythm details: %s", chordRhythmDetails_));
    }

    private List<RhythmDetails> initializeRhythmDetails(ChannelType channelType)
    {
        return RhythmLoader.loadRhythmDetails(scale_.getRhythmSize(), channelType);
    }

    private NextNoteProbabilities initializeNextNoteProbabilities()
    {
        return NextNoteProbabilitiesLoader.loadNoteProbabilities();
    }

    private List<IScale> initializeScales()
    {
        MusicScalesConfig musicScalesConfig = MusicScaleConfigLoader.load();
        return musicScalesConfig.getScales().stream()
                .map(scale -> (IScale) scale)
                .collect(Collectors.toList());
    }

    public void setScale(IScale scale)
    {
        scale_ = scale;
        LOGGER.info(String.format("Set scale: %s", scale.getName()));
        soloInstrumentCode_ = scale_.getSoloInstrument();
        LOGGER.info(String.format("Set melody instrument: %s", soloInstrumentCode_));
        melodyRhythmDetails_ = initializeRhythmDetails(ChannelType.MELODY);
        LOGGER.info(String.format("Set melody rhythm details: %s", melodyRhythmDetails_));
        chordRhythmDetails_ = initializeRhythmDetails(ChannelType.CHORD);
        LOGGER.info(String.format("Set chord rhythm details: %s", chordRhythmDetails_));
    }

    public void setMinutes(int minutes)
    {
        minutes_ = minutes;
        LOGGER.info(String.format("Set minutes: %s", minutes));
    }

    private int initializePartsCount()
    {
        int partsCount = IS_TEST ? 2 : (int) (Math.random() * 4) + 2;
        return partsCount;
    }

    public int getPhraseCount()
    {
        int phraseCount = IS_TEST ? 2 : (int) (Math.random() * 4) + 4;
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
        int channelCount = IS_TEST
                ? testChannelCount_
                : (int) (channelCount_ >= 0
                ? channelCount_
                : Math.random() * 3);
        Synthesizer synthesizer = MusicUtil.getInstance().getSynthesizer();
        int possibleChannelCount = synthesizer.getChannels().length - 1;
        return Math.min(channelCount, possibleChannelCount);
    }

    public static boolean isTest()
    {
        return IS_TEST;
    }

    public NextNoteProbabilities getNextNoteProbabilities()
    {
        return nextNoteProbabilities_;
    }

    public List<RhythmDetails> getChordRhythmDetails()
    {
        return chordRhythmDetails_;
    }

    public List<RhythmDetails> getMelodyRhythmDetails()
    {
        return melodyRhythmDetails_;
    }

    public RhythmDetails getChordPrimaryRhythmDetails()
    {
        return chordPrimaryRhythmDetails_;
    }

    public RhythmDetails getMelodyPrimaryRhythmDetails()
    {
        return melodyPrimaryRhythmDetails_;
    }

    public int getMelodyInstrumentCode()
    {
        return soloInstrumentCode_;
    }

    public IScale getScale()
    {
        return scale_;
    }

    public List<IScale> getScales()
    {
        return scales_;
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
