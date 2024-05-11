package ivko.lana.generators;

import ivko.lana.entities.MajorScale;
import ivko.lana.entities.MinorScale;
import ivko.lana.yaml.RhythmPattern;
import ivko.lana.entities.IScale;
import ivko.lana.yaml.RhythmLoader;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class Initializer
{
    private RhythmPattern rhythmPattern_;
    private int instrumentCode_;
    private IScale scale_;

    private int partsCount_;
    private int channelCount_;
    private boolean isChordSequenced_;

    public Initializer()
    {
        rhythmPattern_ = initializeRhythmPattern();
        scale_ = initializeScale();
        instrumentCode_ = initializeInstrumentCode();
        partsCount_ = initializePartsCount();
        channelCount_ = initializeChannelCount();
        isChordSequenced_ = initializeIsChordSequenced();
    }

    private boolean initializeIsChordSequenced()
    {
        return (int) (Math.random() * 2) == 1;
    }

    private RhythmPattern initializeRhythmPattern()
    {
        List<RhythmPattern> rhythmPatterns = RhythmLoader.loadAllPatterns();
        int length = rhythmPatterns.size();
        int rhythmIndex = (int) (Math.random() * length);
        RhythmPattern rhythmPattern = rhythmPatterns.get(rhythmIndex);
        return rhythmPattern;
    }

    private IScale initializeScale()
    {
        int scaleChoice = (int) (Math.random() * 2);
        boolean isMajor = scaleChoice == 0;
        return isMajor ? new MajorScale() : new MinorScale();
    }

    private int initializeInstrumentCode()
    {
//    int instrumentCode = (int) (Math.random() * 100);
        int instrumentCode = 0;
        return instrumentCode;
    }

    private int initializePartsCount()
    {
    int partsCount = (int) (Math.random() * 10);
//        int partsCount = 2;
        return partsCount * 2;
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
//    int channelCount = (int) (Math.random() * 4) + 4;
        int channelCount = 2;
        return channelCount;
    }

    public RhythmPattern getRhythmPattern()
    {
        return rhythmPattern_;
    }

    public int getInstrumentCode()
    {
        return instrumentCode_;
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
}
