package ivko.lana.tibetian;

import javax.sound.sampled.*;

public class TibetanBowlSound {

    private static final float SAMPLE_RATE = 44100.0f; // Частота дискретизации

    // Метод для создания синусоидальной волны с затуханием и начальным ударом
    private static short[] createSineWaveWithDecay(double frequency, int durationMs, double sampleRate, double amplitude, double decayFactor) {
        int samples = (int) ((durationMs / 1000.0) * sampleRate);
        short[] output = new short[samples];
        double angleIncrement = 2.0 * Math.PI * frequency / sampleRate;

        for (int i = 0; i < samples; i++) {
            double angle = i * angleIncrement;
            double decay = 1.0 - (i / (double) samples); // Линейное затухание

            // Имитация удара
            double impact = 1.0;
            short value = (short) (Math.sin(angle) * amplitude * decay * impact * Short.MAX_VALUE);
            output[i] = value;
        }

        return output;
    }

    public static void main(String[] args) {

        double baseFrequency = 220.0; // Основная частота
        double[] frequencies = {
                baseFrequency,
                baseFrequency * 2.7,
                baseFrequency * 4.8,
                baseFrequency * 7.5
        };



        float sampleRate = SAMPLE_RATE; // Частота дискретизации
        int durationMs = 8000; // Длительность в миллисекундах
//        double[] frequencies = {220.0, 594.0, 1056.0, 1650.0}; // Пропорции частот 1:2.7:4.8:7.5
        double[] amplitudes = {0.5, 0.2, 0.05, 0.02}; // Амплитуды для каждой гармоники
        double decayFactor = 1; // Плавное затухание

        try {
            // Формат аудио (стерео)
            AudioFormat format = new AudioFormat(sampleRate, 16, 2, true, true);

            // Создаем финальный буфер для суммирования гармоник
            int totalSamples = (int) ((durationMs / 1000.0) * sampleRate);
            byte[] finalSound = new byte[totalSamples * 4]; // 4 байта на выборку (2 байта на канал)
            short[] combinedSignal = new short[totalSamples];

            for (int j = 0; j < frequencies.length; j++) {
                short[] soundWave = createSineWaveWithDecay(frequencies[j], durationMs, sampleRate, amplitudes[j], decayFactor);
                for (int i = 0; i < totalSamples; i++) {
                    combinedSignal[i] += soundWave[i]; // Суммируем гармоники
                }
            }

            // Добавляем эффект изменения громкости по каналам
            for (int i = 0; i < totalSamples; i++) {
                double pulsation = ((double) durationMs / 1000) * Math.PI; // Скорость перехода
                double panning = 0.3 + 0.4 * Math.sin(pulsation * i / totalSamples); // Изменение громкости в диапазоне от 0.3 до 0.7

                short leftValue = (short) (combinedSignal[i] * (1.0 - panning));
                short rightValue = (short) (combinedSignal[i] * panning);

                leftValue = (short) Math.max(Math.min(leftValue, Short.MAX_VALUE), Short.MIN_VALUE);
                rightValue = (short) Math.max(Math.min(rightValue, Short.MAX_VALUE), Short.MIN_VALUE);

                finalSound[i * 4] = (byte) (leftValue >> 8);
                finalSound[i * 4 + 1] = (byte) leftValue;
                finalSound[i * 4 + 2] = (byte) (rightValue >> 8);
                finalSound[i * 4 + 3] = (byte) rightValue;
            }

            // Воспроизведение звука
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(finalSound, 0, finalSound.length);
            line.drain();
            line.stop();
            line.close();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
