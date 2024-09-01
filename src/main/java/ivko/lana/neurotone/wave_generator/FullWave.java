package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.Sound;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class FullWave
{
    private static final Logger logger = CustomLogger.getLogger(Sound.class.getName());

    private WaveDetail waveDetail_;

    public FullWave(WaveType waveType, double[][] notes, boolean isLeft)
    {
        double[] scaleDegrees = new double[notes.length];
        double[] durations = new double[notes.length];
        SingleWave[] singleWaves = null;
        for (int i = 0; i < notes.length; i++)
        {
            double scaleDegree = notes[i][0];
            scaleDegrees[i] = scaleDegree;
//            scaleDegree = isLeft ? scaleDegree : scaleDegree + Constants.FrequencyOffset_;
            int durationMs = (int) notes[i][1];
            durations[i] = durationMs;
            SoundsCache soundsCache = SoundsLibrary.getInstance().getSoundsCache(waveType, isLeft, scaleDegree, durationMs);

            if (singleWaves == null)
            {
                singleWaves = new SingleWave[soundsCache.getSize()];
                for (int j = 0; j < singleWaves.length; ++j)
                {
                    singleWaves[j] = new SingleWave(notes.length);
                }
            }
            for (int j = 0; j < singleWaves.length; ++j)
            {
                Sound sound = soundsCache.getAt(j);
                singleWaves[j].addNote(sound, i);
            }
        }

        if (singleWaves != null)
        {
            short[][] fullSamples = new short[singleWaves.length][];
            for (int i = 0; i < singleWaves.length; ++i)
            {
                fullSamples[i] = singleWaves[i].getSamples();
            }
            short[] samples = convertFadedNotesToByteArray(fullSamples);
            waveDetail_ = new WaveDetail(scaleDegrees, durations, samples);
        }
    }

    public WaveDetail getWaveDetail()
    {
        return waveDetail_;
    }

    public short[] convertFadedNotesToByteArray(short[][] samples)
    {
        int totalSamples = samples[0].length;
        short[] combinedSamples = new short[totalSamples];

        for (int i = 0; i < totalSamples; i++)
        {
            int combinedValue = 0;

            // Суммируем значения семплов для каждого обертона
            for (int j = 0; j < samples.length; j++)
            {
                combinedValue += samples[j][i];
            }
            // Ограничиваем значение в пределах допустимых значений для short
            combinedSamples[i] = Util.getLimitedValue(combinedValue);
        }

        return combinedSamples;
    }
}
