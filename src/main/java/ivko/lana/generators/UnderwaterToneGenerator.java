package ivko.lana.generators;

/**
 * @author Lana Ivko
 */

import javax.sound.sampled.*;
import java.util.Random;

public class UnderwaterToneGenerator {

    public static void main(String[] args) throws LineUnavailableException {
        // Настройка аудиоформата
        float sampleRate = 44100;
        byte[] buffer = new byte[2];
        AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();

        // Параметры мелодии
        double toneFactor = 1.5;  // Более высокий коэффициент для мягкой подводной атмосферы
        double duration = 10;  // Длительность в секундах

        // Генерация семплов
        short[] samples = generateUnderwaterMelody(toneFactor, duration, sampleRate);

        // Воспроизведение
        for (int i = 0; i < samples.length; i++) {
            buffer[0] = (byte) (samples[i] & 0xFF);
            buffer[1] = (byte) (samples[i] >> 8);
            sdl.write(buffer, 0, 2);
        }

        sdl.drain();
        sdl.stop();
    }

    // Метод для генерации подводной мелодии
    public static short[] generateUnderwaterMelody(double toneFactor, double duration, float sampleRate) {
        // Массив массивов частот для нескольких нот, играющих одновременно
        double[][] layers = {
                {220.00 * toneFactor, 440.00 * toneFactor},  // Первая нота с гармоникой
                {261.63 * toneFactor, 523.25 * toneFactor},  // Вторая нота с гармоникой
                {329.63 * toneFactor, 659.25 * toneFactor}   // Третья нота с гармоникой
        };

        double[] amplitudes = {0.15, 0.1, 0.08};  // Уменьшенные амплитуды для предотвращения перегрузки
        long numSamples = (long) (sampleRate * duration);
        double[] tempSamples = new double[(int) numSamples];
        short[] samples = new short[(int) numSamples];

        double sampleDuration = 1.0 / sampleRate;
        double time = 0;

        // Генерация семплов
        for (long i = 0; i < numSamples; i++) {
            double signal = 0;

            // Проходим по каждому слою нот
            for (int layer = 0; layer < layers.length; layer++) {
                for (int j = 0; j < layers[layer].length; j++) {
                    double freq = layers[layer][j];
                    double angle = i / (sampleRate / freq) * 2.0 * Math.PI;
                    signal += Math.sin(angle) * amplitudes[layer] * slowFrequencyModulation(time);  // Мягкая модуляция
                }
            }

            // Добавляем реверберацию (эхо)
            if (i > 1000) {
                signal += tempSamples[(int) (i - 1000)] * 0.3;  // Уменьшенная амплитуда эха
            }

            // Ограничиваем сигнал для предотвращения клиппинга
            signal = Math.max(-1.0, Math.min(1.0, signal));
            tempSamples[(int) i] = signal;

            // Преобразуем в short
            samples[(int) i] = (short) (signal * 32767);

            // Обновляем время
            time += sampleDuration;
        }

        return samples;
    }

    // Метод для мягкой модуляции частоты (создает плавающий эффект)
    public static double slowFrequencyModulation(double time) {
        return 1.0 + 0.02 * Math.sin(time * 0.05);  // Медленная синусоидальная модуляция частоты
    }
}