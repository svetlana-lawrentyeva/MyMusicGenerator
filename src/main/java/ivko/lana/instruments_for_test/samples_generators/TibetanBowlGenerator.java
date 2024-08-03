package ivko.lana.instruments_for_test.samples_generators;

import java.util.Random;

/**
 * @author Lana Ivko
 */
public class TibetanBowlGenerator
{
    public static final String FILE_PATH = "tibetan_bowl.wav";

    public static void main(String[] args)
    {
        double frequency = 440; // Частота в Гц
        double amplitude = 0.5; // Амплитуда (0.0 до 1.0)
        int duration = 13; // Длительность в секундах
        int sampleRate = 44100; // Частота дискретизации
        Util.clearPreviousFile(FILE_PATH);
        byte[] sineWave = generateTibetanBowlSound(duration, sampleRate);
        Util.saveWaveFile(sineWave, sampleRate, FILE_PATH);
    }

    public static byte[] generateTibetanBowlSound(int duration, int sampleRate)
    {
        int totalSamples = duration * sampleRate;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)

        double[] frequencies = {385, 1035, 1900, 2935}; // Основные частоты и обертоны
        double[] amplitudes = {0.8, 0.6, 0.4, 0.3}; // Амплитуды для каждой частоты
        double decayFactor = 0.0001; // Фактор затухания для амплитуды
        Random random = new Random();
        int rampSamples = sampleRate; // Плавное нарастание в течение 1 секунды

        for (int i = 0; i < totalSamples; i++)
        {
            double sampleValue = 0.0;
            double rampFactor = (i < rampSamples) ? (double) i / rampSamples : 1.0;

            for (int j = 0; j < frequencies.length; j++)
            {
                double angle = 2.0 * Math.PI * frequencies[j] * i / sampleRate;
                double variation = 0.01 * (random.nextDouble() - 0.5); // Очень небольшие случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j];
            }

            // Применение затухания
//            sampleValue *= Math.exp(-decayFactor * i);
            double decay = Math.pow(Math.exp(-decayFactor * i), 0.5);
            sampleValue *= decay;

            // Применение плавного нарастания
            sampleValue *= rampFactor;

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return output;
    }

}
