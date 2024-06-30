package ivko.lana.instruments_for_test.samples_generators;

/**
 * @author Lana Ivko
 */
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GytarSamplelGenerator
{
    private static final String FILE_PATH = "gytar.wav";
    public static void main(String[] args) {
        int duration = 60; // Длительность в секундах
        int sampleRate = 44100; // Частота дискретизации
        Util.clearPreviousFile(FILE_PATH);
        byte[] gytarSound = generateSound(duration, sampleRate);
        Util.saveWaveFile(gytarSound, sampleRate, FILE_PATH);
    }

    public static byte[] generateSound(int duration, int sampleRate) {
        int totalSamples = duration * sampleRate;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)

        double[] frequencies = {440, 660, 880, 1100, 1320, 1540, 1760}; // Основные частоты и обертоны
        double[] amplitudes = {0.5, 0.4, 0.3, 0.2, 0.15, 0.1, 0.05}; // Амплитуды для каждой частоты
        double decayFactor = 0.0002; // Фактор затухания для амплитуды

        for (int i = 0; i < totalSamples; i++) {
            double sampleValue = 0.0;

            for (int j = 0; j < frequencies.length; j++) {
                double angle = 2.0 * Math.PI * frequencies[j] * i / sampleRate;
                sampleValue += Math.sin(angle) * amplitudes[j];
            }

            // Применение затухания
            sampleValue *= Math.exp(-decayFactor * i);

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return output;
    }
}


