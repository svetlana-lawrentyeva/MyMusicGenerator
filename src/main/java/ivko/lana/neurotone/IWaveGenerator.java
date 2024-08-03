package ivko.lana.neurotone;

import ivko.lana.neurotone.wave_generator.WaveDetail;

/**
 * @author Lana Ivko
 */
public interface IWaveGenerator
{
    boolean generateMusic();

    WaveDetail getLeftChannel();

    WaveDetail getRightChannel();
}
