package ivko.lana.instruments_for_test.samples_generators;

import java.util.Random;

/**
 * @author Lana Ivko
 */
public class TibetanBowlSoundWithDipsGenerator_2
{
    public static final String FILE_PATH = "tibetan_2_bowl_sound_with_dips_2.wav";

    public static void main(String[] args)
    {
        int duration = 20; // Длительность в секундах
        int sampleRate = 44100; // Частота дискретизации
        Util.clearPreviousFile(FILE_PATH);
        byte[] sineWave = generateTibetanBowlSound(duration, sampleRate);
        Util.saveWaveFile(sineWave, sampleRate, FILE_PATH);
    }

    public static byte[] generateTibetanBowlSound(int duration, int sampleRate)
    {
        int totalSamples = duration * sampleRate;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)

        double[] frequencies = {377.6, 1024, 1888, 2919}; // Основные частоты и обертоны
        double[] amplitudes = {0.256, 0.056, 0.01, 0.005}; // Амплитуды для каждой частоты
        double[] dips = {0.8, 1.0, 1.0, 1.0}; // Углубления для каждой частоты
        int[] decayFactors = {1, 1, 1, 1};

        double decayFactor = 0.00005; // Фактор затухания для амплитуды
        Random random = new Random();
        int rampSamples = sampleRate; // Плавное нарастание в течение 1 секунды
        double modulationDepth = 0.1; // Глубина модуляции амплитуды

        int currentSample = 0;

        // Фаза нарастания
        currentSample = applyAttackPhase(output, currentSample, rampSamples, sampleRate, frequencies, amplitudes, dips, random);

        // Фаза удержания
        currentSample = applySustainPhase(output, rampSamples, rampSamples, sampleRate, frequencies, amplitudes, dips, random);

        // Фаза спада
        applyDecayPhase(output, currentSample, totalSamples - 2 * rampSamples, sampleRate, frequencies, amplitudes, dips, decayFactors, random);

        // Применение модуляции амплитуды ко всему массиву
//        output = applyAmplitudeModulation(output, 14.5, modulationDepth, sampleRate);
        output = applyAmplitudeModulation(output, 1.18, modulationDepth, sampleRate);

        return output;
    }

    private static int applyAttackPhase(byte[] output, int startSample, int rampSamples, int sampleRate,
                                        double[] frequencies, double[] amplitudes, double[] dips, Random random)
    {
        for (int i = 0; i < rampSamples; i++)
        {
            double sampleValue = 0.0;
            double rampFactor = (double) i / rampSamples;

            for (int j = 0; j < frequencies.length; j++)
            {
                double angle = 2.0 * Math.PI * frequencies[j] * (startSample + i) / sampleRate;
                double variation = 0.002 * (random.nextDouble() - 0.5); // Уменьшенные случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j] * dips[j];
            }

            sampleValue *= rampFactor;
            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[(startSample + i) * 2] = (byte) (sample & 0xff);
            output[(startSample + i) * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return startSample + rampSamples;
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
