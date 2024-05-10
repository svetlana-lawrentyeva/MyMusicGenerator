package ivko.lana.MelodyGenerator;

import ivko.lana.entities.RhythmPattern;
import ivko.lana.util.RhythmLoader;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class Initializer
{
    private RhythmPattern rhythmPattern_;
    private boolean isMajor_;
    private int instrumentCode_;

    public Initializer()
    {
        rhythmPattern_ = initializeRhythmPattern();
        isMajor_ = initializeIsMajor();
        instrumentCode_ = initializeInstrumentCode();
    }

    private RhythmPattern initializeRhythmPattern()
    {
        List<RhythmPattern> rhythmPatterns = RhythmLoader.loadAllPatterns();
        int length = rhythmPatterns.size();
        int rhythmIndex = (int) (Math.random() * length);
        RhythmPattern rhythmPattern = rhythmPatterns.get(rhythmIndex);
        return rhythmPattern;
    }

    private boolean initializeIsMajor()
    {
        int scaleChoice = (int) (Math.random() * 2);
        boolean isMajor = scaleChoice == 0;
        return isMajor;
    }

    private int initializeInstrumentCode()
    {

//    int instrumentCode = random.nextInt(100);
        int instrumentCode = 0;
        return instrumentCode;
    }

    public RhythmPattern getRhythmPattern()
    {
        return rhythmPattern_;
    }

    public boolean isMajor()
    {
        return isMajor_;
    }

    public int getInstrumentCode()
    {
        return instrumentCode_;
    }
}
