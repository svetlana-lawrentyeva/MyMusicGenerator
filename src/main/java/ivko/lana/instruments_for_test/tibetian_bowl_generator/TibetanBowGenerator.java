package ivko.lana.instruments_for_test.tibetian_bowl_generator;

import ivko.lana.instruments_for_test.samples_generators.Util;

import java.util.Random;

/**
 * @author Lana Ivko
 */
public class TibetanBowGenerator
{
    public static final String FILE_PATH = "tibetan_2_bowl_sound_with_dips_2.wav";
    private static final int SAMPLE_RATE = 44100;

    public static void main(String[] args)
    {
        int duration = 20; // Длительность в секундах
        Util.clearPreviousFile(FILE_PATH);
        byte[] sineWave = generateTibetanBowlSound(duration, 377.6);
        Util.saveWaveFile(sineWave, SAMPLE_RATE, FILE_PATH);
    }

    public static byte[] generateTibetanBowlSound(int duration, double frequency)
    {
        int totalSamples = duration * SAMPLE_RATE;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)

        double[] frequencies = {frequency, frequency * 2.71, frequency * 5.00, frequency * 7.73}; // Основные частоты и обертоны
        double[] amplitudes = {0.256, 0.056, 0.01, 0.005}; // Амплитуды для каждой частоты
        double[] dips = {0.8, 1.0, 1.0, 1.0}; // Углубления для каждой частоты
        int[] decayFactors = {1, 1, 1, 1};

        Random random = new Random();
        int rampSamples = SAMPLE_RATE; // Плавное нарастание в течение 1 секунды
        double modulationDepth = 0.1; // Глубина модуляции амплитуды

        // Фаза удержания
        int currentSample = applySustainPhase(output, rampSamples, rampSamples, SAMPLE_RATE, frequencies, amplitudes, dips, random);

        // Фаза спада
        applyDecayPhase(output, currentSample, totalSamples - 2 * rampSamples, SAMPLE_RATE, frequencies, amplitudes, dips, decayFactors, random);

        // Применение модуляции амплитуды ко всему массиву
        output = applyAmplitudeModulation(output, 1.18, modulationDepth, SAMPLE_RATE);

        return output;
    }

    private static int applySustainPhase(byte[] output, int startSample, int sustainSamples, int sampleRate,
                                         double[] frequencies, double[] amplitudes, double[] dips, Random random)
    {
        for (int i = 0; i < sustainSamples; i++)
        {
            double sampleValue = 0.0;

            for (int j = 0; j < frequencies.length; j++)
            {
                double angle = 2.0 * Math.PI * frequencies[j] * (startSample + i) / sampleRate;
                double variation = 0.002 * (random.nextDouble() - 0.5); // Уменьшенные случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j] * dips[j];
            }

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[(startSample + i) * 2] = (byte) (sample & 0xff);
            output[(startSample + i) * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return startSample + sustainSamples;
    }

    private static void applyDecayPhase(byte[] output, int startSample, int rampSamples, int sampleRate,
                                        double[] frequencies, double[] amplitudes, double[] dips, int[] decayTypes, Random random)
    {
        for (int i = 0; i < rampSamples; i++)
        {
            double sampleValue = 0.0;

            for (int j = 0; j < frequencies.length; j++)
            {
                double angle = 2.0 * Math.PI * frequencies[j] * (startSample + i) / sampleRate;
                double variation = 0.005 * (random.nextDouble() - 0.5); // Уменьшенные случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j] * dips[j];

                // Применение различных типов затухания
                double decayFactorAdjusted = 1.0;
                switch (decayTypes[j])
                {
                    case 1:
                        // Линейное затухание
                        decayFactorAdjusted = 1.0 - ((double) i / rampSamples);
                        break;
                    case 2:
                        // Логарифмическое затухание
                        decayFactorAdjusted = Math.log((double) (rampSamples - i) / rampSamples + 1) / Math.log(2);
                        break;
                    case 3:
                        // Экспоненциальное затухание
                        decayFactorAdjusted = Math.exp(-0.00005 * i);
                        break;
                    default:
                        break;
                }

                sampleValue *= decayFactorAdjusted;
            }

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[(startSample + i) * 2] = (byte) (sample & 0xff);
            output[(startSample + i) * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }
    }

    public static byte[] applyAmplitudeModulation(byte[] input, double modulationFrequency, double modulationDepth, int sampleRate)
    {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length / 2; i++)
        {
            short sample = (short) (((input[i * 2 + 1] & 0xff) << 8) | (input[i * 2] & 0xff));
            double modulation = 1.0 + modulationDepth * Math.sin(2.0 * Math.PI * modulationFrequency * i / sampleRate);
            sample = (short) (sample * modulation);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }
        return output;
    }
}
