package ivko.lana.instruments_for_test.tibetian_bowl_generator;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/**
 * Генератор звука тибетской поющей чаши.
 *
 * Алгоритм считает обертоны из фундаментальной частоты и задаёт
 * относительные уровни/затухания, чтобы получить характерный тембр чаши
 * без внешних библиотек — только стандартный Java Sound API.
 */
public class TibetanBowlSoundGenerator
{
    /**
     * Отношения частот основных частичных (взяты из усреднённых анализов звучания чаш).
     * Частоты не идеально гармоничны, что добавляет «металлический» характер звука.
     */
    private static final double[] PARTIAL_RATIOS = {1.0, 2.03, 2.45, 2.85, 3.6, 4.25};

    /**
     * Уровни громкости обертонов (в дБ относительно фундаментальной).
     * Чем выше обертон, тем слабее его звучание.
     */
    private static final double[] PARTIAL_LEVELS_DB = {0.0, -5.0, -9.0, -12.0, -17.0, -22.0};

    /**
     * Время затухания каждого обертона (в секундах). Высокие частоты затухают быстрее.
     */
    private static final double[] PARTIAL_DECAY = {4.0, 3.5, 3.0, 2.3, 1.6, 1.2};

    /**
     * Небольшой шум удара, чтобы звук не был стерильно-синтетическим.
     */
    private static final double STRIKE_NOISE_DB = -28.0;

    /**
     * Глобальный коэффициент усиления для предотвращения клиппинга даже при суммировании фаз.
     */
    private static final double MASTER_GAIN = 0.9;

    private static final Random RANDOM = new Random(42L);

    static
    {
        ensureSameLength(PARTIAL_RATIOS, PARTIAL_LEVELS_DB, PARTIAL_DECAY);
    }

    public static void main(String[] args)
    {
        double fundamental = args.length > 0 ? Double.parseDouble(args[0]) : 196.0; // G3 по умолчанию
        double durationSeconds = args.length > 1 ? Double.parseDouble(args[1]) : 4.0;
        int sampleRate = 44_100;

        byte[] audioData = generateBowlSound(fundamental, sampleRate, durationSeconds);
        File outputFile = new File(String.format("tibetan_bowl_%sHz.wav", Math.round(fundamental)));
        try
        {
            writeWav(audioData, sampleRate, outputFile);
            System.out.printf("Файл успешно создан: %s%n", outputFile.getAbsolutePath());
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Не удалось сохранить звук чаши", e);
        }
    }

    /**
     * Синтезирует сэмплы звука чаши с обертонами и экспоненциальным затуханием.
     */
    public static byte[] generateBowlSound(double fundamentalHz, int sampleRate, double durationSeconds)
    {
        int numSamples = (int) (durationSeconds * sampleRate);
        byte[] pcm = new byte[numSamples * 2]; // 16 бит = 2 байта на сэмпл

        for (int i = 0; i < numSamples; i++)
        {
            double time = i / (double) sampleRate;
            double sample = 0.0;

            for (int partialIndex = 0; partialIndex < PARTIAL_RATIOS.length; partialIndex++)
            {
                double freq = fundamentalHz * PARTIAL_RATIOS[partialIndex];
                double amplitude = dbToAmplitude(PARTIAL_LEVELS_DB[partialIndex]);
                double decayEnvelope = Math.exp(-time / PARTIAL_DECAY[partialIndex]);
                double vibrato = 1.0 + 0.005 * Math.sin(2 * Math.PI * 5.0 * time); // лёгкая живость тона

                sample += amplitude * decayEnvelope * Math.sin(2 * Math.PI * freq * time * vibrato);
            }

            // добавляем короткий шум удара (около 40 мс)
            double strikeEnvelope = Math.exp(-time / 0.04);
            double strikeNoise = (RANDOM.nextDouble() * 2.0 - 1.0) * dbToAmplitude(STRIKE_NOISE_DB) * strikeEnvelope;
            sample += strikeNoise;

            // Мягкая общая огибающая атаки
            double attackEnvelope = 1.0 - Math.exp(-time * 40.0);
            sample *= attackEnvelope;

            // Предотвращаем клиппинг: нормализуем и ограничиваем диапазон [-1;1]
            sample = Math.tanh(sample * MASTER_GAIN);

            short sampleShort = (short) (sample * Short.MAX_VALUE);
            pcm[2 * i] = (byte) (sampleShort & 0xff);
            pcm[2 * i + 1] = (byte) ((sampleShort >> 8) & 0xff);
        }

        return pcm;
    }

    private static double dbToAmplitude(double db)
    {
        return Math.pow(10.0, db / 20.0);
    }

    private static void ensureSameLength(double[]... arrays)
    {
        Objects.requireNonNull(arrays, "Массивы для проверки не должны быть null");
        int expected = arrays[0].length;
        for (int i = 1; i < arrays.length; i++)
        {
            if (arrays[i].length != expected)
            {
                throw new IllegalArgumentException("Длины массивов частичных должны совпадать");
            }
        }
    }

    private static void writeWav(byte[] audioData, int sampleRate, File outputFile) throws IOException
    {
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        try (AudioInputStream audioInputStream = new AudioInputStream(bais, format,
                audioData.length / format.getFrameSize()))
        {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        }
    }
}

