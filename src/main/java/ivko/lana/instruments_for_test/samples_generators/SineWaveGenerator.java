package ivko.lana.instruments_for_test.samples_generators;

/**
 * @author Lana Ivko
 */

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SineWaveGenerator {

    public static final String FILE_PATH = "sine_wave.wav";

    public static void main(String[] args) {
        double frequency = 440; // Частота в Гц
        double amplitude = 0.5; // Амплитуда (0.0 до 1.0)
        int duration = 2; // Длительность в секундах
        int sampleRate = 44100; // Частота дискретизации
        Util.clearPreviousFile(FILE_PATH);
        byte[] sineWave = generateSineWave(frequency, amplitude, duration, sampleRate);
        Util.saveWaveFile(sineWave, sampleRate, FILE_PATH);
    }

    public static byte[] generateSineWave(double frequency, double amplitude, int duration, int sampleRate) {
        int totalSamples = duration * sampleRate;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)
        double angleIncrement = 2.0 * Math.PI * frequency / sampleRate;

        for (int i = 0; i < totalSamples; i++) {
            double angle = i * angleIncrement;
            short sample = (short) (Math.sin(angle) * amplitude * Short.MAX_VALUE);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return output;
    }
}


