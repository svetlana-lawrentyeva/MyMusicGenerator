package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.wave_generator.sounds.SoundType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class SoundsLibrary
{
    private final Map<Double, Map<Integer, SoundsCache>> leftSoundsLibrary_;
    private final Map<Double, Map<Integer, SoundsCache>> rightSoundsLibrary_;
    private SoundType soundType_;

    private SoundsLibrary()
    {
        soundType_ = Constants.SOUND_TYPE;
        leftSoundsLibrary_ = new HashMap<>();
        rightSoundsLibrary_ = new HashMap<>();
    }

    public static SoundsLibrary getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    public SoundsCache getSoundsCache(WaveType waveType, boolean isLeft, double scaleDegree, int durationMs)
    {
        Map<Double, Map<Integer, SoundsCache>> library = isLeft ? leftSoundsLibrary_ : rightSoundsLibrary_;
        Map<Integer, SoundsCache> durationsMap = library.get(scaleDegree);
        if (durationsMap == null)
        {
            durationsMap = new HashMap<>();
        }
        library.put(scaleDegree, durationsMap);

        SoundsCache soundsCache = durationsMap.get(durationMs);
        if (soundsCache == null)
        {
            soundsCache = new SoundsCache(waveType, soundType_, scaleDegree, durationMs, isLeft);
            durationsMap.put(durationMs, soundsCache);
        }
        return soundsCache;
    }

    private static class InstanceHolder
    {
        private static final SoundsLibrary INSTANCE = new SoundsLibrary();
    }
}
