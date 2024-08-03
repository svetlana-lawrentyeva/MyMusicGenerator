package ivko.lana.neurotone.wave_generator.sounds;

import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleOvertonesHelper;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleSamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanOvertonesHelper;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanSamplesCreator;

/**
 * @author Lana Ivko
 */
public enum SoundType
{
    TIBETAN {@Override public ISamplesCreator getSamplesCreator() {return new TibetanSamplesCreator();}@Override public IOvertoneHelper getOvertoneHelper() {return new TibetanOvertonesHelper();}},
    SIMPLE {@Override public ISamplesCreator getSamplesCreator() {return new SimpleSamplesCreator();}@Override public IOvertoneHelper getOvertoneHelper() {return new SimpleOvertonesHelper();}};

    public abstract ISamplesCreator getSamplesCreator();
    public abstract IOvertoneHelper getOvertoneHelper();
}
