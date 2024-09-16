package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.wave_generator.sounds.SoundType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class TibetanHitSoundLibrary
{
    private final Map<Double, TibetanHitSound> hitsLibrary_;

    private TibetanHitSoundLibrary()
    {
        hitsLibrary_ = new HashMap<>();
    }

    public static TibetanHitSoundLibrary getInstance()
    {
        return TibetanHitSoundLibrary.InstanceHolder.INSTANCE;
    }

    public TibetanHitSound getHitSound(SoundType soundType, double frequency)
    {
        TibetanHitSound hitCache = hitsLibrary_.get(frequency);
        if (hitCache == null)
        {
            hitCache = new TibetanHitSound(soundType, frequency);
            hitsLibrary_.put(frequency, hitCache);
        }
        return hitCache;
    }

    private static class InstanceHolder
    {
        private static final TibetanHitSoundLibrary INSTANCE = new TibetanHitSoundLibrary();
    }
}
