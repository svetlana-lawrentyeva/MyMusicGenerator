package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.audio_generator.AudioSaver;
import ivko.lana.neurotone.util.CustomLogger;

import java.util.logging.Logger;

public class FrequencyConverter
{
    private static final Logger logger = CustomLogger.getLogger(AudioSaver.class.getName());

    private FullWave leftFullMelody_;
    private FullWave rightFullMelody_;

    public void convert(double[][] notes)
    {
        leftFullMelody_ = new FullWave(notes, true);
        rightFullMelody_ = new FullWave(notes, false);
    }

    public WaveDetail getLeftChannel()
    {
        return leftFullMelody_.getWaveDetail();
    }

    public WaveDetail getRightChannel()
    {
        return rightFullMelody_.getWaveDetail();
    }

}
