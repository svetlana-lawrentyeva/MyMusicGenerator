package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.audio_generator.AudioSaver;
import ivko.lana.neurotone.util.CustomLogger;

import java.util.logging.Logger;

public class FrequencyConverter
{
    private static final Logger logger = CustomLogger.getLogger(AudioSaver.class.getName());

    private WaveType waveType_;
    private FullWave leftFullMelody_;
    private FullWave rightFullMelody_;

    public FrequencyConverter(WaveType waveType)
    {
        waveType_ = waveType;
    }

    public void convert(double[][] notes)
    {
        leftFullMelody_ = new FullWave(waveType_, notes, true);
        rightFullMelody_ = new FullWave(waveType_, notes, false);
    }

    public void convert(double[][] leftNotes, double[][] rightNotes)
    {
        leftFullMelody_ = new FullWave(waveType_, leftNotes, true);
        rightFullMelody_ = new FullWave(waveType_, rightNotes, false);
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
