package ivko.lana.instruments_for_test.samples_generators;

/**
 * @author Lana Ivko
 */

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SingingBowlGenerator {

    public static final String FILE_PATH = "singing_bowl.wav";

    public static void main(String[] args) {
        int duration = 10; // Длительность в секундах
        int sampleRate = 44100; // Частота дискретизации
        Util.clearPreviousFile(FILE_PATH);
        byte[] bowlSound = generateSingingBowlSound(duration, sampleRate);
        Util.saveWaveFile(bowlSound, sampleRate, FILE_PATH);
    }

    public static byte[] generateSingingBowlSound(int duration, int sampleRate) {
        int totalSamples = duration * sampleRate;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)

        double[] frequencies = {220, 440, 660, 880, 1100, 1320, 1540, 1760, 1980, 2200}; // Основные частоты и обертоны
        double[] amplitudes = {0.5, 0.4, 0.3, 0.2, 0.15, 0.1, 0.08, 0.06, 0.04, 0.03}; // Амплитуды для каждой частоты
        double decayFactor = 0.00003; // Фактор затухания для амплитуды
        Random random = new Random();
        int rampSamples = sampleRate / 4; // Плавное нарастание в течение 1/4 секунды

        for (int i = 0; i < totalSamples; i++) {
            double sampleValue = 0.0;
            double rampFactor = (i < rampSamples) ? (double) i / rampSamples : 1.0;

            for (int j = 0; j < frequencies.length; j++) {
                double angle = 2.0 * Math.PI * frequencies[j] * i / sampleRate;
                double variation = 0.001 * (random.nextDouble() - 0.5); // Очень небольшие случайные колебания
                sampleValue += Math.sin(angle + variation) * amplitudes[j];
            }

            // Применение затухания
            sampleValue *= Math.exp(-decayFactor * i);

            // Применение плавного нарастания
            sampleValue *= rampFactor;

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return output;
    }
}


