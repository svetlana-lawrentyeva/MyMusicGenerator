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
    private final Map<Double, SoundsCache> hitsLibrary_;
    private SoundType soundType_;

    private SoundsLibrary()
    {
        soundType_ = Constants.SOUND_TYPE;
        leftSoundsLibrary_ = new HashMap<>();
        rightSoundsLibrary_ = new HashMap<>();
        hitsLibrary_ = new HashMap<>();
    }

    public static SoundsLibrary getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    public SoundsCache getSoundsCache(boolean isLeft, double frequency, int durationMs)
    {
        Map<Double, Map<Integer, SoundsCache>> library = isLeft ? leftSoundsLibrary_ : rightSoundsLibrary_;
        Map<Integer, SoundsCache> durationsMap = library.get(frequency);
        if (durationsMap == null)
        {
            durationsMap = new HashMap<>();
        }
        library.put(frequency, durationsMap);

        SoundsCache soundsCache = durationsMap.get(durationMs);
        if (soundsCache == null)
        {
            soundsCache = new SoundsCache(soundType_, frequency, durationMs, isLeft);
            durationsMap.put(durationMs, soundsCache);
        }
        return soundsCache;
    }

    public SoundsCache getHitsCache(double frequency)
    {
        SoundsCache hitCache = hitsLibrary_.get(frequency);
        if (hitCache == null)
        {
            hitCache = new SoundsCache(soundType_, frequency, 0, true);
            hitsLibrary_.put(frequency, hitCache);
        }
        return hitCache;
    }

    private static class InstanceHolder
    {
        private static final SoundsLibrary INSTANCE = new SoundsLibrary();
    }
}
