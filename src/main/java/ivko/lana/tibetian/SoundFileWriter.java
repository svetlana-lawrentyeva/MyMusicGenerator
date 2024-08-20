package ivko.lana.tibetian;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SoundFileWriter {

    // Метод для записи звука в WAV-файл
    private static void writeSoundToFile(String filename, short[] sound) {
        try {
            AudioFormat format = new AudioFormat(TibetanBowlSound.SAMPLE_RATE, 16, 2, true, true);
            byte[] finalSound = new byte[sound.length * 4];

            for (int i = 0; i < sound.length; i++) {
                finalSound[i * 4] = (byte) (sound[i] >> 8);
                finalSound[i * 4 + 1] = (byte) sound[i];
                finalSound[i * 4 + 2] = (byte) (sound[i] >> 8);
                finalSound[i * 4 + 3] = (byte) sound[i];
            }

            AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(finalSound), format, sound.length);
            File wavFile = new File(filename);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);

            System.out.println("Аудиофайл записан: " + wavFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        double[][] notes = {
                {220.0, 3000}, // Частота и длительность в миллисекундах
                {440.0, 5000},
                {330.0, 2000}
        };

        TibetanBowlSound.setPulsationDepth(0.3f);
        TibetanBowlSound.setPulsationRateFactor(0.2f);

        // Воспроизведение звуков
        for (double[] note : notes) {
            TibetanBowlSound.play(note[0], (int) note[1]);
        }

        // Вычисляем общее количество сэмплов, включая перекрытие затухания
        int totalSamples = 0;
        int fadeOutSamples = (int) (TibetanBowlSound.FADE_OUT_DURATION_MS / 1000.0 * TibetanBowlSound.SAMPLE_RATE);
        for (int i = 0; i < notes.length; i++) {
            int durationMs = (int) notes[i][1];
            totalSamples += (int) ((durationMs / 1000.0) * TibetanBowlSound.SAMPLE_RATE);
            if (i > 0) {
                // Добавляем сэмплы затухания, если это не первая нота
                totalSamples += fadeOutSamples;
            }
        }

        short[] fullSound = new short[totalSamples];
        int offset = 0;

        // Запись звуков в файл
        for (int i = 0; i < notes.length; i++) {
            double frequency = notes[i][0];
            int durationMs = (int) notes[i][1];

            // Генерация звукового сигнала для записи
            short[] sound = TibetanBowlSound.createHarmonicSignal(frequency, durationMs);

            // Копирование звукового сигнала в общий массив, включая перекрытие
            for (int j = 0; j < sound.length; j++) {
                if (offset + j < fullSound.length) {
                    // Уменьшаем амплитуду перед сложением
                    int mixedValue = (int) (fullSound[offset + j] * 0.8 + sound[j] * 0.8);
                    fullSound[offset + j] = (short) Math.max(Math.min(mixedValue, Short.MAX_VALUE), Short.MIN_VALUE); // Ограничиваем диапазон
                }
            }
            offset += (int) ((durationMs / 1000.0) * TibetanBowlSound.SAMPLE_RATE);
        }

        // Нормализация сигнала после сложения
        fullSound = normalize(fullSound);

        // Запись всего звука в единый файл
        writeSoundToFile("output.wav", fullSound);
    }

    // Метод для нормализации амплитуды
    private static short[] normalize(short[] samples) {
        double maxAmplitude = 0;
        for (short sample : samples) {
            maxAmplitude = Math.max(maxAmplitude, Math.abs(sample));
        }

        double scaleFactor = Short.MAX_VALUE / maxAmplitude;
        short[] normalized = new short[samples.length];

        for (int i = 0; i < samples.length; i++) {
            normalized[i] = (short) (samples[i] * scaleFactor);
        }

        return normalized;
    }
}
