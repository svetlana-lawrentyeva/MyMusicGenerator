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
            double decay = Math.exp(-decayFactor * i / sampleRate); // Экспоненциальное затухание

            // Имитация удара
            double impact = 1.0;
            short value = (short) (Math.sin(angle) * amplitude * decay * impact * Short.MAX_VALUE);
            output[i] = value;
        }

        return output;
    }

    public static void main(String[] args) {
        float sampleRate = SAMPLE_RATE; // Частота дискретизации
        int durationMs = 16000; // Длительность в миллисекундах
        double[] frequencies = {220.0, 594.0, 1056.0, 1650.0}; // Пропорции частот 1:2.7:4.8:7.5
        double[] amplitudes = {0.5, 0.2, 0.05, 0.02}; // Амплитуды для каждой гармоники
        double decayFactor = 0.8; // Плавное затухание

        try {
            // Формат аудио
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);

            // Создаем финальный буфер для суммирования гармоник
            int totalSamples = (int) ((durationMs / 1000.0) * sampleRate);
            byte[] finalSound = new byte[totalSamples * 2];
            short[] combinedSignal = new short[totalSamples];

            for (int j = 0; j < frequencies.length; j++) {
                short[] soundWave = createSineWaveWithDecay(frequencies[j], durationMs, sampleRate, amplitudes[j], decayFactor);
                for (int i = 0; i < totalSamples; i++) {
                    combinedSignal[i] += soundWave[i]; // Суммируем гармоники
                }
            }

            // Ограничиваем итоговый сигнал и переводим его в байты
            for (int i = 0; i < totalSamples; i++) {
                combinedSignal[i] = (short) Math.max(Math.min(combinedSignal[i], Short.MAX_VALUE), Short.MIN_VALUE);
                finalSound[i * 2] = (byte) (combinedSignal[i] >> 8);
                finalSound[i * 2 + 1] = (byte) combinedSignal[i];
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
