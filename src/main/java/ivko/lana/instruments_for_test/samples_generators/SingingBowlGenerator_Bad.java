package ivko.lana.instruments_for_test.samples_generators;

/**
 * @author Lana Ivko
 */

public class SingingBowlGenerator_Bad
{

    public static final String FILE_PATH = "singing_bowl.wav";

    public static void main(String[] args)
    {
        int duration = 10; // Длительность в секундах
        int sampleRate = 44100; // Частота дискретизации
        Util.clearPreviousFile(FILE_PATH);
        byte[] bowlSound = generateSingingBowlSound(duration, sampleRate);
        Util.saveWaveFile(bowlSound, sampleRate, FILE_PATH);
    }

    public static byte[] generateSingingBowlSound(int duration, int sampleRate)
    {
        int totalSamples = duration * sampleRate;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)

        double[] frequencies = {220, 440, 660, 880, 1100}; // Основные частоты и обертоны
        double[] amplitudes = {0.5, 0.4, 0.3, 0.2, 0.1}; // Амплитуды для каждой частоты
        double maxAmplitude = 0.5; // Ограничение максимальной амплитуды
        double exponentialDecayFactor = Math.log(0.01) / totalSamples; // Экспоненциальное затухание для всей длительности
        int rampSamples = sampleRate; // Плавное нарастание в течение 1 секунды
        int oscillationPeriod = sampleRate; // Период колебания амплитуды

        for (int i = 0; i < totalSamples; i++)
        {
            double sampleValue = 0.0;
            double rampFactor = (i < rampSamples) ? Math.sin((Math.PI / 2) * ((double) i / rampSamples)) : 1.0; // Плавное экспоненциальное нарастание

            for (int j = 0; j < frequencies.length; j++)
            {
                double angle = 2.0 * Math.PI * frequencies[j] * i / sampleRate;
                sampleValue += Math.sin(angle) * amplitudes[j] * Math.exp(exponentialDecayFactor * i); // Экспоненциальное затухание
            }

            // Колебание амплитуды после достижения пика
            double oscillationFactor = 1.0 + 0.1 * Math.sin(2 * Math.PI * i / oscillationPeriod); // Колебание от 0.9 до 1.1
            sampleValue *= oscillationFactor;

            // Применение плавного нарастания
            sampleValue *= rampFactor;

            // Ограничение максимальной амплитуды
            if (sampleValue > maxAmplitude)
            {
                sampleValue = maxAmplitude;
            }
            else if (sampleValue < -maxAmplitude)
            {
                sampleValue = -maxAmplitude;
            }

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return output;
    }
}


