package ivko.lana.instruments_for_test.samples_generators;

/**
 * @author Lana Ivko
 */

import java.util.Random;

public class SingingBowlGenerator
{
    private static final double MIN_AMPLITUDE = 0.5; // Минимальная амплитуда затухания
    private static final double MAX_AMPLITUDE = 0.75; // Максимальная амплитуда для пиков

    public static final String FILE_PATH = "singing_bowl.wav";

    public static void main(String[] args)
    {
        int duration = 360; // Длительность в секундах
        int sampleRate = 44100; // Частота дискретизации
        Util.clearPreviousFile(FILE_PATH);
        byte[] bowlSound = generateSingingBowlSound(duration, sampleRate, 1, 5);
        Util.saveWaveFile(bowlSound, sampleRate, FILE_PATH);
    }

    public static byte[] generateSingingBowlSound(int duration, int sampleRate, int attackDuration, int decayDuration)
    {
        int totalSamples = duration * sampleRate;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)

        int rampSamples = attackDuration * sampleRate; // Плавное нарастание в течение attackDuration секунд
        int decaySamples = decayDuration * sampleRate; // Плавное затухание в течение decayDuration секунд
        int sustainSamples = totalSamples - rampSamples - decaySamples; // Количество сэмплов, когда звук на уровне амплитуды без изменения

        int currentSample = 0;
        currentSample = applyAttackPhase(output, currentSample, rampSamples, sampleRate);
        currentSample = applySustainPhase(output, currentSample, sustainSamples, sampleRate);
        output = applyNonLinearDecay(output, sampleRate, MIN_AMPLITUDE);
        applyDecayPhase(output, currentSample, decaySamples, sampleRate);

        return output;
    }


    private static int applyAttackPhase(byte[] output, int startSample, int rampSamples, int sampleRate)
    {
        double[] frequencies = {220, 440, 660, 880, 1100, 1320, 1540, 1760, 1980, 2200};
        double[] amplitudes = {0.5, 0.4, 0.3, 0.2, 0.15, 0.1, 0.08, 0.06, 0.04, 0.03};
        Random random = new Random();

        for (int i = 0; i < rampSamples; i++)
        {
            double sampleValue = 0.0;
            double rampFactor = (double) i / rampSamples;

            for (int j = 0; j < frequencies.length; j++)
            {
                double angle = 2.0 * Math.PI * frequencies[j] * (startSample + i) / sampleRate;
                double variation = 0.01 * (random.nextDouble() - 0.5); // Очень небольшие случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j];
            }

            sampleValue *= rampFactor * MAX_AMPLITUDE; // Применение уменьшения максимальной амплитуды
            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[(startSample + i) * 2] = (byte) (sample & 0xff);
            output[(startSample + i) * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return startSample + rampSamples;
    }


    private static int applySustainPhase(byte[] output, int startSample, int sustainSamples, int sampleRate)
    {
        double[] frequencies = {220, 440, 660, 880, 1100, 1320, 1540, 1760, 1980, 2200};
        double[] amplitudes = {0.5, 0.4, 0.3, 0.2, 0.15, 0.1, 0.08, 0.06, 0.04, 0.03};
        double warbleFrequency = 2.0; // Частота воя (5 Гц)
        double warbleDepth = 0.05; // Глубина воя (насколько изменяется амплитуда)
        Random random = new Random();

        int cycleDuration = 4 * sampleRate; // Длительность одного цикла затухания-нарастания в сэмплах (увеличена до 4 секунд)
        int halfCycle = cycleDuration / 2;

        for (int i = 0; i < sustainSamples; i++)
        {
            double sampleValue = 0.0;
            double cyclePosition = (i % cycleDuration) / (double) halfCycle;
            double rampFactor;

            if (i % cycleDuration < halfCycle)
            {
                // Фаза затухания
                rampFactor = MAX_AMPLITUDE - (MAX_AMPLITUDE - MIN_AMPLITUDE) * cyclePosition;
            }
            else
            {
                // Фаза нарастания
                rampFactor = MIN_AMPLITUDE + (MAX_AMPLITUDE - MIN_AMPLITUDE) * (cyclePosition - 1.0);
            }

            // Применение воя (warble effect)
            double warble = 1.0 + warbleDepth * Math.sin(2.0 * Math.PI * warbleFrequency * i / sampleRate);

            for (int j = 0; j < frequencies.length; j++)
            {
                double angle = 2.0 * Math.PI * frequencies[j] * (startSample + i) / sampleRate;
                double variation = 0.01 * (random.nextDouble() - 0.5); // Очень небольшие случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j];
            }

            sampleValue *= rampFactor * warble;

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[(startSample + i) * 2] = (byte) (sample & 0xff);
            output[(startSample + i) * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return startSample + sustainSamples;
    }


    public static byte[] applyNonLinearDecay(byte[] output, int sampleRate, double minAmplitude)
    {
        int totalSamples = output.length / 2; // Количество сэмплов
        double decayFactor = Math.log(minAmplitude) / totalSamples; // Фактор затухания для амплитуды

        for (int i = 0; i < totalSamples; i++)
        {
            double sampleValue = ((output[i * 2 + 1] << 8) | (output[i * 2] & 0xff)) / (double) Short.MAX_VALUE;

            // Применение нелинейного затухания
            double decay = Math.exp(decayFactor * i);
            sampleValue *= decay;

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return output;
    }


    private static void applyDecayPhase(byte[] output, int startSample, int decaySamples, int sampleRate)
    {
        double[] frequencies = {220, 440, 660, 880, 1100, 1320, 1540, 1760, 1980, 2200};
        double[] amplitudes = {0.5, 0.4, 0.3, 0.2, 0.15, 0.1, 0.08, 0.06, 0.04, 0.03};
        double decayFactor = 0.00003; // Фактор затухания для амплитуды
        double minAmplitude = 0.5; // Минимальная амплитуда после затухания
        Random random = new Random();

        for (int i = 0; i < decaySamples; i++)
        {
            double sampleValue = 0.0;
            double rampFactor = 1.0 - (double) i / decaySamples;

            for (int j = 0; j < frequencies.length; j++)
            {
                double angle = 2.0 * Math.PI * frequencies[j] * (startSample + i) / sampleRate;
                double variation = 0.01 * (random.nextDouble() - 0.5); // Очень небольшие случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j];
            }

            // Применение затухания, чтобы оно сводилось к minAmplitude, а не к нулю
            double decay = (1 - minAmplitude) * Math.exp(-decayFactor * (startSample + i)) + minAmplitude;
            sampleValue *= decay;

            sampleValue *= rampFactor;
            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[(startSample + i) * 2] = (byte) (sample & 0xff);
            output[(startSample + i) * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }
    }

}


