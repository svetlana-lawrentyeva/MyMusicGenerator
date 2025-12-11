package ivko.lana.neurotone.wave_generator.sounds;

import ivko.lana.neurotone.wave_generator.sounds.mixed_sound.MixedSoundCreator;
import ivko.lana.neurotone.wave_generator.sounds.mixed_sound.MixedSoundHelper;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleOvertonesHelper;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleSamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanOvertonesHelper;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanSamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.violin.ViolinOvertonesHelper;
import ivko.lana.neurotone.wave_generator.sounds.violin.ViolinSamplesCreator;

/**
 * @author Lana Ivko
 */
public enum SoundType
{
    TIBETAN {@Override public ISamplesCreator getSamplesCreator() {return new TibetanSamplesCreator();}@Override public IOvertoneHelper getOvertoneHelper() {return new TibetanOvertonesHelper();}},
    SIMPLE {@Override public ISamplesCreator getSamplesCreator() {return new SimpleSamplesCreator();}@Override public IOvertoneHelper getOvertoneHelper() {return new SimpleOvertonesHelper();}},
    VIOLIN {@Override public ISamplesCreator getSamplesCreator() {return new ViolinSamplesCreator();}@Override public IOvertoneHelper getOvertoneHelper() {return new ViolinOvertonesHelper();}},
    MIXED_SOUND {@Override public ISamplesCreator getSamplesCreator() {return new MixedSoundCreator();}@Override public IOvertoneHelper getOvertoneHelper() {return new MixedSoundHelper();}};

    public abstract ISamplesCreator getSamplesCreator();
    public abstract IOvertoneHelper getOvertoneHelper();
}
