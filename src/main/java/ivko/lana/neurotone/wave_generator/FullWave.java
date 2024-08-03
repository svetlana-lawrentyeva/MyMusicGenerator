package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.Sound;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class FullWave
{
    private static final Logger logger = CustomLogger.getLogger(Sound.class.getName());

    private WaveDetail waveDetail_;

    public FullWave(double[][] notes, boolean isLeft)
    {
        double[] frequencies = new double[notes.length];
        double[] durations = new double[notes.length];
        SingleMelody[] singleMelodies = null;
        for (int i = 0; i < notes.length; i++)
        {
            double frequency = notes[i][0];
            frequencies[i] = frequency;
            frequency = isLeft ? frequency : frequency + Constants.FrequencyOffset_;
            int durationMs = (int) notes[i][1];
            durations[i] = durationMs;
            SoundsCache soundsCache = SoundsLibrary.getInstance().getSoundsCache(isLeft, frequency, durationMs);

            if (singleMelodies == null)
            {
                singleMelodies = new SingleMelody[soundsCache.getSize()];
                for (int j = 0; j < singleMelodies.length; ++j)
                {
                    singleMelodies[j] = new SingleMelody(notes.length);
                }
            }
            for (int j = 0; j < singleMelodies.length; ++j)
            {
                Sound sound = soundsCache.getAt(j);
                singleMelodies[j].addNote(sound, i);
            }
        }

        if (singleMelodies != null)
        {
            short[][] fullSamples = new short[singleMelodies.length][];
            for (int i = 0; i < singleMelodies.length; ++i)
            {
                fullSamples[i] = singleMelodies[i].getSamples();
            }
            short[] samples = convertFadedNotesToByteArray(fullSamples);
            waveDetail_ = new WaveDetail(frequencies, durations, samples);
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
