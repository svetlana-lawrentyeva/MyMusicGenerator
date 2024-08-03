package ivko.lana.tibetian;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TibetanBowlSound1 {

    // Метод для создания синусоидальной волны с экспоненциальным затуханием и легким шумом
    private static byte[] createSineWaveWithDecay(double frequency, int durationMs, double sampleRate, double amplitude, double decayFactor) {
        int samples = (int) ((durationMs / 1000.0) * sampleRate);
        byte[] output = new byte[samples * 2];
        double angleIncrement = 2.0 * Math.PI * frequency / sampleRate;

        for (int i = 0; i < samples; i++) {
            double angle = i * angleIncrement;
            double decay = Math.exp(-decayFactor * i / sampleRate); // Экспоненциальное затухание
            short value = (short) (Math.sin(angle) * amplitude * decay * Short.MAX_VALUE);

            // Добавляем легкий шум для смягчения
            value += (short) ((Math.random() - 0.5) * amplitude * 0.02 * Short.MAX_VALUE);

            byte[] bytes = ByteBuffer.allocate(2).putShort(value).array();
            output[i * 2] = bytes[0];
            output[i * 2 + 1] = bytes[1];
        }

        return output;
    }

    public static void main(String[] args) {
        float sampleRate = 44100.0f; // Частота дискретизации
        int durationMs = 8000; // Длительность в миллисекундах
        double[] frequencies = {220.0, 440.0, 660.0}; // Основная частота и обертоны (та же высота звука)
        double[] amplitudes = {0.6, 0.3, 0.15}; // Амплитуды для каждой гармоники
        double decayFactor = 0.05; // Плавное затухание

        try {
            // Формат аудио
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);

            // Объединение гармоник
            byte[] finalSound = new byte[(int) ((durationMs / 1000.0) * sampleRate * 2)];
            Arrays.fill(finalSound, (byte) 0);

            for (int j = 0; j < frequencies.length; j++) {
                byte[] soundWave = createSineWaveWithDecay(frequencies[j], durationMs, sampleRate, amplitudes[j], decayFactor);
                for (int i = 0; i < finalSound.length; i++) {
                    finalSound[i] += soundWave[i] / frequencies.length; // Усредняем гармоники
                }
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
