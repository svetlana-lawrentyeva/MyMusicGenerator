package ivko.lana.tibetian;

import javax.sound.sampled.*;

public class TibetanBowlSound {

    public static final float SAMPLE_RATE = 44100.0f; // Частота дискретизации

    // Метод для создания одной синусоидальной волны с затуханием
    private static short[] createSineWaveWithDecay(double frequency, int durationMs, double sampleRate, double amplitude) {
        int samples = (int) ((durationMs / 1000.0) * sampleRate);
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

    // Метод для создания полного звукового сигнала из гармоник
    public static short[] createHarmonicSignal(double baseFrequency, int durationMs) {
        double[] frequencies = {
                baseFrequency,
                baseFrequency * 2.7,
                baseFrequency * 4.8,
                baseFrequency * 7.5
        };

        double[] amplitudes = {0.5, 0.2, 0.05, 0.02}; // Амплитуды для каждой гармоники
        int totalSamples = (int) ((durationMs / 1000.0) * SAMPLE_RATE);
        short[] combinedSignal = new short[totalSamples];

        for (int j = 0; j < frequencies.length; j++) {
            short[] soundWave = createSineWaveWithDecay(frequencies[j], durationMs, SAMPLE_RATE, amplitudes[j]);
            for (int i = 0; i < totalSamples; i++) {
                combinedSignal[i] += soundWave[i]; // Суммируем гармоники
            }
        }

        return combinedSignal;
    }

    // Метод для проигрывания звука
    private static void playSound(short[] sound, int durationMs) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, true);
            int totalSamples = sound.length;
            byte[] finalSound = new byte[totalSamples * 4]; // 4 байта на выборку (2 байта на канал)

            // Добавляем эффект изменения громкости по каналам
            for (int i = 0; i < totalSamples; i++) {
                double pulsation = ((double) durationMs / 1000) * Math.PI; // Скорость перехода
                double panning = 0.3 + 0.4 * Math.sin(pulsation * i / totalSamples); // Изменение громкости в диапазоне от 0.3 до 0.7

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
        short[] sound = createHarmonicSignal(baseFrequency, durationMs);
        playSound(sound, durationMs);
    }
}
