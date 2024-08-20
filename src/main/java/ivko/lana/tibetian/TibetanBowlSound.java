package ivko.lana.tibetian;

import javax.sound.sampled.*;

public class TibetanBowlSound {

    public static final float SAMPLE_RATE = 44100.0f; // Частота дискретизации
    public static final int FADE_OUT_DURATION_MS = 5000; // Длительность затухания
    private static float pulsationDepth = 0.5f; // Параметр для управления пульсацией (от 0 до 1)
    private static float pulsationRateFactor = 1.0f; // Коэффициент для управления скоростью пульсации

    // Метод для установки глубины пульсации
    public static void setPulsationDepth(float depth) {
        if (depth < 0.0f) depth = 0.0f;
        if (depth > 1.0f) depth = 1.0f;
        pulsationDepth = depth;
    }

    // Метод для установки скорости пульсации
    public static void setPulsationRateFactor(float rateFactor) {
        if (rateFactor < 0.1f) rateFactor = 0.1f;
        if (rateFactor > 10.0f) rateFactor = 10.0f;
        pulsationRateFactor = rateFactor;
    }

    // Метод для создания одной синусоидальной волны с постоянной громкостью
    private static short[] createConstantWave(double frequency, int durationMs, double sampleRate, double amplitude) {
        int samples = (int) ((durationMs / 1000.0) * sampleRate);
        short[] output = new short[samples];
        double angleIncrement = 2.0 * Math.PI * frequency / sampleRate;

        for (int i = 0; i < samples; i++) {
            double angle = i * angleIncrement;
            short value = (short) (Math.sin(angle) * amplitude * Short.MAX_VALUE);
            output[i] = value;
        }

        return output;
    }

    // Метод для создания синусоидальной волны с затуханием
    private static short[] createFadeOutWave(double frequency, double sampleRate, double amplitude, int fadeOutDurationMs) {
        int samples = (int) ((fadeOutDurationMs / 1000.0) * sampleRate);
        short[] output = new short[samples];
        double angleIncrement = 2.0 * Math.PI * frequency / sampleRate;

        for (int i = 0; i < samples; i++) {
            double angle = i * angleIncrement;
            double decay = 1.0 - (i / (double) samples); // Линейное затухание
            short value = (short) (Math.sin(angle) * amplitude * decay * Short.MAX_VALUE);
            output[i] = value;
        }

        return output;
    }

    // Метод для создания полного звукового сигнала из гармоник с затуханием
    public static short[] createHarmonicSignal(double baseFrequency, int durationMs) {
        double[] frequencies = {
                baseFrequency,
                baseFrequency * 2.7,
                baseFrequency * 4.8,
                baseFrequency * 7.5
        };

        double[] amplitudes = {0.5, 0.2, 0.05, 0.02}; // Амплитуды для каждой гармоники
        int totalSamples = (int) ((durationMs / 1000.0) * SAMPLE_RATE);
        int fadeOutSamples = (int) ((FADE_OUT_DURATION_MS / 1000.0) * SAMPLE_RATE);
        short[] combinedSignal = new short[totalSamples + fadeOutSamples];

        // Создаем основной сигнал без затухания
        for (int j = 0; j < frequencies.length; j++) {
            short[] constantWave = createConstantWave(frequencies[j], durationMs, SAMPLE_RATE, amplitudes[j]);
            for (int i = 0; i < totalSamples; i++) {
                combinedSignal[i] += constantWave[i]; // Суммируем гармоники
            }
        }

        // Добавляем затухание в конец сигнала
        for (int j = 0; j < frequencies.length; j++) {
            short[] fadeOutWave = createFadeOutWave(frequencies[j], SAMPLE_RATE, amplitudes[j], FADE_OUT_DURATION_MS);
            for (int i = 0; i < fadeOutSamples; i++) {
                combinedSignal[totalSamples + i] += fadeOutWave[i]; // Суммируем затухание
            }
        }

        return combinedSignal;
    }

    // Метод для проигрывания звука
    private static void playSound(short[] sound, double baseFrequency) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, true);
            int totalSamples = sound.length;
            byte[] finalSound = new byte[totalSamples * 4]; // 4 байта на выборку (2 байта на канал)

            // Базовая скорость пульсации
            double basePulsationSpeed = baseFrequency / 100.0;

            // Добавляем эффект изменения громкости по каналам
            for (int i = 0; i < totalSamples; i++) {
                double time = i / SAMPLE_RATE; // Текущее время в секундах
                double pulsation = pulsationRateFactor * basePulsationSpeed * time; // Скорость перехода
                double panning = 0.5 + 0.5 * pulsationDepth * Math.sin(2 * Math.PI * pulsation); // Управление пульсацией

                short leftValue = (short) (sound[i] * (1.0 - panning));
                short rightValue = (short) (sound[i] * panning);

                leftValue = (short) Math.max(Math.min(leftValue, Short.MAX_VALUE), Short.MIN_VALUE);
                rightValue = (short) Math.max(Math.min(rightValue, Short.MAX_VALUE), Short.MIN_VALUE);

                finalSound[i * 4] = (byte) (leftValue >> 8);
                finalSound[i * 4 + 1] = (byte) leftValue;
                finalSound[i * 4 + 2] = (byte) (rightValue >> 8);
                finalSound[i * 4 + 3] = (byte) rightValue;
            }

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

    // Метод для воспроизведения звука с заданной частотой и длительностью
    public static void play(double baseFrequency, int durationMs) {
        new Thread(() -> {
            short[] sound = createHarmonicSignal(baseFrequency, durationMs);
            playSound(sound, baseFrequency);
        }).start();

        // Задержка основного потока на длительность основной части звука
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
