package ivko.lana.neurotone.wave_generator.sounds.simple;

import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.ISamplesCreator;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class SimpleSamplesCreator implements ISamplesCreator
{
    private static final Logger logger = CustomLogger.getLogger(SimpleSamplesCreator.class.getName());

    private SimpleClearSamplesCreator clearSamplesCreator_;

    public SimpleSamplesCreator()
    {
        clearSamplesCreator_ = createClearSamplesCreator();
    }

    protected SimpleClearSamplesCreator createClearSamplesCreator()
    {
        return new SimpleClearSamplesCreator();
    }

    protected SimpleClearSamplesCreator getClearSamplesCreator()
    {
        return clearSamplesCreator_;
    }

    public short[] createSamples(int durationMs, double frequency, double amplitude, boolean isLeft, double phaseMultiplier, int overtoneIndex)
    {
        return clearSamplesCreator_.createClearSamples(durationMs, frequency, amplitude);
    }
}
