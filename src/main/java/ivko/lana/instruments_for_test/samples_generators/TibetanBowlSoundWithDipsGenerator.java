package ivko.lana.instruments_for_test.samples_generators;

import java.util.Random;

/**
 * @author Lana Ivko
 */
public class TibetanBowlSoundWithDipsGenerator
{
    public static final String FILE_PATH = "tibetan_bowl_sound_with_dips.wav";

    public static void main(String[] args)
    {
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

        double[] frequencies = {372, 1100, 1300, 1500, 1700, 1900, 2100, 2950}; // Основные частоты и обертоны
        double[] amplitudes = {0.256, 0.056, 0.045, 0.035, 0.025, 0.015, 0.01, 0.01}; // Амплитуды для каждой частоты
        double[] dips = {0.8, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 0.3}; // Углубления для каждой частоты

        double decayFactor = 0.00005; // Фактор затухания для амплитуды
        Random random = new Random();
        int rampSamples = sampleRate; // Плавное нарастание в течение 1 секунды
        double modulationDepth = 0.1; // Увеличенная глубина модуляции амплитуды

        int currentSample = 0;

        // Фаза нарастания
        currentSample = applyAttackPhase(output, currentSample, rampSamples, sampleRate, frequencies, amplitudes, dips, random);

        // Фаза удержания
        currentSample = applySustainPhase(output, currentSample, rampSamples, sampleRate, frequencies, amplitudes, dips, random);

        // Фаза спада
        applyDecayPhase(output, currentSample, totalSamples - 2 * rampSamples, sampleRate, frequencies, amplitudes, dips, decayFactor, random);

        // Применение модуляции амплитуды ко всему массиву
        output = applyAmplitudeModulation(output, 14.5, modulationDepth, sampleRate);
        output = applyAmplitudeModulation(output, 1.18, modulationDepth, sampleRate);

        return output;
    }

    private static int applyAttackPhase(byte[] output, int startSample, int rampSamples, int sampleRate,
                                        double[] frequencies, double[] amplitudes, double[] dips, Random random)
    {
        for (int i = 0; i < rampSamples; i++) {
            double sampleValue = 0.0;
            double rampFactor = (double) i / rampSamples;

            for (int j = 0; j < frequencies.length; j++) {
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
        for (int i = 0; i < sustainSamples; i++) {
            double sampleValue = 0.0;

            for (int j = 0; j < frequencies.length; j++) {
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
                                        double[] frequencies, double[] amplitudes, double[] dips, double decayFactor, Random random)
    {
        for (int i = 0; i < rampSamples; i++) {
            double sampleValue = 0.0;
            double decayFactorAdjusted = Math.exp(-decayFactor * (i + 1)); // Сразу затухает после пика

            for (int j = 0; j < frequencies.length; j++) {
                double angle = 2.0 * Math.PI * frequencies[j] * (startSample + i) / sampleRate;
                double variation = 0.002 * (random.nextDouble() - 0.5); // Уменьшенные случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j] * dips[j];
            }

            sampleValue *= decayFactorAdjusted;
            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[(startSample + i) * 2] = (byte) (sample & 0xff);
            output[(startSample + i) * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }
    }

    public static byte[] applyAmplitudeModulation(byte[] input, double modulationFrequency, double modulationDepth, int sampleRate)
    {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length / 2; i++) {
            short sample = (short) (((input[i * 2 + 1] & 0xff) << 8) | (input[i * 2] & 0xff));
            double modulation = 1.0 + modulationDepth * Math.sin(2.0 * Math.PI * modulationFrequency * i / sampleRate);
            sample = (short) (sample * modulation);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }
        return output;
    }
}
