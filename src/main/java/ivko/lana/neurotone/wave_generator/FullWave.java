package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.Sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Lana Ivko
 */
public class FullWave
{
    private static final Logger logger = CustomLogger.getLogger(Sound.class.getName());

    private WaveDetail waveDetail_;
    private boolean isLeft_;

    public FullWave(WaveType waveType, double[][] notes, boolean isLeft)
    {
        isLeft_ = isLeft;
        double[] scaleDegrees = new double[notes.length];
        double[] durations = new double[notes.length];
        SingleWave[] singleWaves = null;
        for (int i = 0; i < notes.length; i++)
        {
            double scaleDegree = notes[i][0];
            scaleDegrees[i] = scaleDegree;
//            scaleDegree = isLeft ? scaleDegree : scaleDegree + Constants.FrequencyOffset_;
            int durationMs = (int) notes[i][1];
            durations[i] = durationMs;
            SoundsCache soundsCache = SoundsLibrary.getInstance().getSoundsCache(waveType, isLeft, scaleDegree, durationMs);

            if (singleWaves == null)
            {
                singleWaves = new SingleWave[soundsCache.getSize()];
                for (int j = 0; j < singleWaves.length; ++j)
                {
                    singleWaves[j] = new SingleWave(notes.length);
                }
            }
            for (int j = 0; j < singleWaves.length; ++j)
            {
                Sound sound = soundsCache.getAt(j);
                singleWaves[j].addNote(sound, i);
            }
        }

        if (singleWaves != null)
        {
            short[][] fullSamples = new short[singleWaves.length][];
            for (int i = 0; i < singleWaves.length; ++i)
            {
                fullSamples[i] = singleWaves[i].getSamples();
            }
            short[] samples = convertFadedNotesToByteArray(fullSamples);
            if (Constants.NEED_ADDITIONAL_EFFECTS)
            {
                short[] additionalEffects = getAdditionalEffects(samples.length);
//                additionalEffects = Util.applyFadeInOut(additionalEffects, Constants.FADE_IN_DURATION_MS, Constants.FadeOutDurationMs_, 0, 0);
//                samples = additionalEffects;
                samples = Util.combineSamples(samples, additionalEffects);
            }
            logger.info(String.format("Created wave detail for %s seconds (%s seconds fade out)", samples.length / Constants.SAMPLE_RATE, Constants.FadeOutDurationMs_));
            waveDetail_ = new WaveDetail(scaleDegrees, durations, samples);
        }
    }

    private short[] getAdditionalEffects(int length)
    {
        short[] result = new short[length];
        Path parentDirectory = Paths.get(Constants.ADDITIONAL_EFFECTS_PATH);

        // Используем Files.walk для обхода всех элементов по пути
        try (var effectFolder = Files.walk(parentDirectory))
        {
            List<Path> effectFolders = effectFolder
                    .filter(Files::isDirectory)  // Фильтруем только директории
                    .filter(path -> path.getParent().equals(parentDirectory))  // Исключаем родительскую директорию
                    .collect(Collectors.toList());// Собираем все пути в список

            Random random = new Random();
            int effectCount = random.nextInt(effectFolders.size() - 1) + 1;

            List<Integer> indices = IntStream.range(0, effectFolders.size())
                    .boxed()
                    .collect(Collectors.toList());
            Collections.shuffle(indices);
            List<Integer> effectIndices = indices.subList(0, effectCount);
            List<short[]> effects = effectIndices.stream()
                    .map(effectFolders::get)
                    .map(folder -> applyRandomEffect(length, folder, random))
                    .collect(Collectors.toList());
            for (short[] effect : effects)
            {
                result = Util.combineSamples(result, effect);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }

    private short[] applyRandomEffect(int length, Path directoryPath, Random random)
    {
        short[] result = new short[length];
        try
        {
            List<Path> files = Files.list(directoryPath)
                    .filter(Files::isRegularFile)  // Фильтруем только файлы, исключаем папки
                    .collect(Collectors.toList());
            int randomIndex = random.nextInt(files.size() - 1) + 1;
            Path path = files.get(randomIndex);

            short[] additionalEffect = readWavFile(path.toString());
            int startIndex = 0;
            while (startIndex + additionalEffect.length <= length)
            {
                System.arraycopy(additionalEffect, 0, result, startIndex, additionalEffect.length);
                startIndex += length;
            }
            if (startIndex < length)
            {
                System.arraycopy(additionalEffect, 0, result, startIndex, length - startIndex);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }

    private short[] readWavFile(String filePath)
    {
        logger.info(String.format("Adding to %s effect from file: %s", isLeft_ ? "left" : "right", filePath));
        short[] audioSamples;
        try
        {
            File wavFile = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
            AudioFormat format = audioInputStream.getFormat();

            // Проверяем количество каналов
            int numChannels = format.getChannels();
            byte[] audioBytes = audioInputStream.readAllBytes();  // Чтение всех байтов

            // Создание массива шортов с учётом количества каналов
            audioSamples = new short[audioBytes.length / 2 / numChannels];  // 2 байта на один сэмпл на канал

            // Конвертация байтов в шорты (16-bit PCM)
            for (int i = 0; i < audioSamples.length; i++)
            {
                int sampleIndex = i * numChannels;
                int leftChannel = (audioBytes[2 * sampleIndex + 1] << 8) | (audioBytes[2 * sampleIndex] & 0xFF);

                if (numChannels > 1)
                {
                    // Если есть правый канал
                    int rightChannel = (audioBytes[2 * sampleIndex + 3] << 8) | (audioBytes[2 * sampleIndex + 2] & 0xFF);
                    // Здесь можно объединить каналы, если нужно (например, усреднить)
                    audioSamples[i] = (short) ((leftChannel + rightChannel) / 2);
                }
                else
                {
                    // Если только один канал (моно)
                    audioSamples[i] = (short) leftChannel;
                }
            }
        }
        catch (UnsupportedAudioFileException | IOException e)
        {
            throw new RuntimeException(e);
        }

        return audioSamples;
    }


    public WaveDetail getWaveDetail()
    {
        return waveDetail_;
    }

    public short[] convertFadedNotesToByteArray(short[][] samples)
    {
        int totalSamples = samples[0].length;
        short[] combinedSamples = new short[totalSamples];

        for (int i = 0; i < totalSamples; i++)
        {
            int combinedValue = 0;

            // Суммируем значения семплов для каждого обертона
            for (int j = 0; j < samples.length; j++)
            {
                combinedValue += samples[j][i];
            }
            // Ограничиваем значение в пределах допустимых значений для short
            combinedSamples[i] = Util.getLimitedValue(combinedValue);
        }

        return combinedSamples;
    }
}
